
package com.vbuilds.dto;

public class CreateBuildRequest {

    private String title;
    private int horsepower;
    private int happiness;
    private int reliability;
    private int loudness;
    private int cost;
    private String tuner;
    private int buildYear;
    private String trim;
    private String mainPhoto;
    private String description;

    public CreateBuildRequest() {}

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public int getHorsepower() { return horsepower; }
    public void setHorsepower(int horsepower) { this.horsepower = horsepower; }

    public int getHappiness() { return happiness; }
    public void setHappiness(int happiness) { this.happiness = happiness; }

    public int getReliability() { return reliability; }
    public void setReliability(int reliability) { this.reliability = reliability; }

    public int getLoudness() { return loudness; }
    public void setLoudness(int loudness) { this.loudness = loudness; }

    public int getCost() { return cost; }
    public void setCost(int cost) { this.cost = cost; }

    public String getTuner() { return tuner; }
    public void setTuner(String tuner) { this.tuner = tuner; }

    public int getBuildYear() { return buildYear; }
    public void setBuildYear(int buildYear) { this.buildYear = buildYear; }

    public String getTrim() { return trim; }
    public void setTrim(String trim) { this.trim = trim; }

    public String getMainPhoto() { return mainPhoto; }
    public void setMainPhoto(String mainPhoto) { this.mainPhoto = mainPhoto; }
}
