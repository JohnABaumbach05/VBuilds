package com.vbuilds.model;

public class Part {
    private String category;
    private int partID;
    private String manufacturer;
    private String partName;
    private int partReliability;
    private int estPowerAdded;
    private String partDescription;
    private int partCost;
    private boolean makesLouder;

    public Part() {}

    public Part(String category, String manufacturer,
                String partName, String partDescription)
    {
        this.category = category; this.manufacturer = manufacturer;
        this.partName = partName; this.partDescription = partDescription; }


    // getters
    public String getCategory() { return category; }

    public int getPartID() { return partID; }

    public String getManufacturer() { return manufacturer; }

    public String getPartName() { return partName; }

    public int getPartReliability() { return partReliability; }

    public int getEstPowerAdded() { return estPowerAdded; }

    public String getPartDescription() { return partDescription; }

    public int getPartCost() { return partCost; }

    public boolean isMakesLouder() { return makesLouder; }

    //setters
    public void setCategory( String newCategory) { this.category = newCategory; }

    public void setManufacturer(String newManufacturer) { this.manufacturer = newManufacturer; }

    public void setPartName( String newPartName) { this.partName = newPartName; }

    public void setPartReliability(int newPartReliability) {
        if (newPartReliability >= 0 && newPartReliability <= 10) { this.partReliability = newPartReliability; } }

    public void setEstPowerAdded(int newEstPowerAdded) { if (newEstPowerAdded >= 0) { this.estPowerAdded = newEstPowerAdded; } }

    public void setPartDescription(String newDescription) { this.partDescription = newDescription; }

    public void setPartCost(int newCost) { if(newCost >= 0) { this.partCost = newCost; } }

    public void setMakesLouder(boolean makesLouder) { this.makesLouder = makesLouder; }

    public void setPartID(int partID) { this.partID = partID; }
}
