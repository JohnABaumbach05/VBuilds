document.addEventListener("DOMContentLoaded", async () => {
    const buildId = new URLSearchParams(location.search).get("id");
    const form = document.getElementById("create-build-form");
    const status = document.getElementById("editStatus");
    const saveButton = form.querySelector('button[type="submit"]');
    const categorySelect = document.getElementById("partCategory");
    const customWrap = document.getElementById("customCategoryWrap");
    const customInput = document.getElementById("customCategory");
    const list = document.getElementById("addedPartsList");
    const categoryOptions = Array.from(categorySelect.options).map(o => o.value);
    let parts = [];
    let editingPartId = null;

    if (!buildId || !/^\d+$/.test(buildId)) {
        status.textContent = "Invalid build ID.";
        form.hidden = true;
        return;
    }
    const endpoint = `/api/builds/${buildId}`;
    const request = async (url, method, body) => {
        const response = await fetch(url, {
            method, credentials: "include",
            ...(body === undefined ? {} : {
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(body)
            })
        });
        if (!response.ok) throw new Error(`${method} failed (${response.status}): ${await response.text()}`);
        return response.status === 204 ? null : response;
    };
    const field = id => document.getElementById(id);
    const setCategory = value => {
        categorySelect.value = categoryOptions.includes(value) && value !== "__custom__" ? value : "__custom__";
        const custom = categorySelect.value === "__custom__";
        customWrap.hidden = !custom;
        customInput.required = custom;
        customInput.value = custom ? value : "";
    };
    categorySelect.addEventListener("change", () => {
        const custom = categorySelect.value === "__custom__";
        customWrap.hidden = !custom;
        customInput.required = custom;
        if (!custom) customInput.value = "";
    });
    const clearPartForm = () => {
        editingPartId = null;
        for (const id of ["partName", "partManufacturer", "partCost", "partDescription"])
            field(id).value = "";
        field("partReliability").value = 5;
        field("partPower").value = 0;
        field("partLouder").checked = false;
        categorySelect.value = "";
        customWrap.hidden = true;
        customInput.required = false;
        customInput.value = "";
        field("addPartButton").textContent = "Add Part";
    };
    const render = () => {
        list.replaceChildren();
        for (const part of parts) {
            const card = document.createElement("article");
            card.className = "added-part-card";
            const heading = document.createElement("h3");
            heading.textContent = part.partName;
            const info = document.createElement("p");
            info.textContent = `${part.category} · ${part.manufacturer} · $${part.partCost}`;
            const edit = document.createElement("button");
            edit.type = "button";
            edit.textContent = "Edit";
            edit.addEventListener("click", () => {
                editingPartId = part.partID;
                setCategory(part.category);
                field("partName").value = part.partName;
                field("partManufacturer").value = part.manufacturer;
                field("partCost").value = part.partCost;
                field("partDescription").value = part.partDescription || "";
                field("partReliability").value = part.partReliability;
                field("partPower").value = part.estPowerAdded;
                field("partLouder").checked = part.makesLouder;
                field("addPartButton").textContent = "Save Part Changes";
                field("partName").focus();
            });
            const remove = document.createElement("button");
            remove.type = "button";
            remove.textContent = "Remove";
            remove.addEventListener("click", async () => {
                if (!confirm(`Remove ${part.partName} from this build?`)) return;
                remove.disabled = true;
                try {
                    await request(`${endpoint}/parts/${part.partID}`, "DELETE");
                    parts = parts.filter(p => p.partID !== part.partID);
                    if (editingPartId === part.partID) clearPartForm();
                    render();
                    status.textContent = "Part removed.";
                } catch (error) {
                    remove.disabled = false;
                    status.textContent = error.message;
                }
            });
            card.append(heading, info, edit, document.createTextNode(" "), remove);
            list.appendChild(card);
        }
    };
    field("addPartButton").addEventListener("click", async () => {
        const category = (categorySelect.value === "__custom__" ? customInput.value : categorySelect.value).trim();
        const part = {
            category,
            partName: field("partName").value.trim(),
            manufacturer: field("partManufacturer").value.trim(),
            partCost: Number(field("partCost").value || 0),
            partDescription: field("partDescription").value.trim(),
            partReliability: Number(field("partReliability").value),
            estPowerAdded: Number(field("partPower").value),
            makesLouder: field("partLouder").checked
        };
        if (!part.category || part.category.length > 60 || !part.partName || !part.manufacturer ||
            !Number.isInteger(part.partCost) || part.partCost < 0 ||
            !Number.isInteger(part.partReliability) || part.partReliability < 0 || part.partReliability > 10 ||
            !Number.isInteger(part.estPowerAdded) || part.estPowerAdded < 0) {
            status.textContent = "Enter a valid category, name, manufacturer, and numeric values.";
            return;
        }
        const button = field("addPartButton");
        button.disabled = true;
        try {
            await request(editingPartId ? `${endpoint}/parts/${editingPartId}` : `${endpoint}/parts`,
                editingPartId ? "PUT" : "POST", part);
            const response = await request(`${endpoint}/parts`, "GET");
            parts = await response.json();
            clearPartForm();
            render();
            status.textContent = "Part saved.";
        } catch (error) {
            status.textContent = error.message;
        } finally {
            button.disabled = false;
        }
    });
    try {
        const response = await request(`${endpoint}/edit`, "GET");
        const build = await response.json();
        field("buildTitle").value = build.title || "";
        field("year").value = build.buildYear;
        field("trim").value = build.trim;
        field("horsepower").value = build.horsepower;
        field("tuner").value = build.tuner || "";
        field("buildCost").value = build.cost;
        field("caption").value = build.description || "";
        for (const rating of ["happiness", "reliability", "loudness"]) {
            field(rating).value = build[rating];
            field(rating + "Value").textContent = build[rating];
            field(rating).addEventListener("input", () => {
                field(rating + "Value").textContent = field(rating).value;
            });
        }
        const partsResponse = await request(`${endpoint}/parts`, "GET");
        parts = await partsResponse.json();
        render();
    } catch (error) {
        status.textContent = "Unable to load build: " + error.message;
        saveButton.disabled = true;
        return;
    }
    form.addEventListener("submit", async event => {
        event.preventDefault();
        saveButton.disabled = true;
        try {
            await request(endpoint, "PUT", {
                title: field("buildTitle").value.trim(),
                buildYear: Number(field("year").value),
                trim: field("trim").value,
                horsepower: Number(field("horsepower").value || 220),
                tuner: field("tuner").value.trim(),
                cost: Number(field("buildCost").value || 0),
                description: field("caption").value.trim(),
                happiness: Number(field("happiness").value),
                reliability: Number(field("reliability").value),
                loudness: Number(field("loudness").value)
            });
            location.href = `/HTML/build.html?id=${buildId}`;
        } catch (error) {
            status.textContent = error.message;
            saveButton.disabled = false;
        }
    });
});
