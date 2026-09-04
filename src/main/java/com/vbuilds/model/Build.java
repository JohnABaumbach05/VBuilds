package com.vbuilds.model;

public class Build {
    private int ownerID;
    private String title;
    private int horsepower;
    private int happiness;
    private int reliability;
    private int loudness;
    private int buildCost;
    private String tuner;
    private int buildYear;
    private String trim;
    private int buildId;
    private boolean featured;


    public Build() {}

    public Build(int ownerId, String title, int hp, String tuner,
                 int year, String trim, int buildId)
    {
        this.ownerID = ownerId; this.title = title; this.horsepower = hp;
        this.tuner = tuner; this.trim = trim; this.buildId = buildId; }

    // getters
    public boolean isFeatured() { return featured; }

    public void setFeatured(boolean featured) { this.featured = featured; }


    public int getOwnerID() { return ownerID; }

    public String getTitle() { return title; }

    public int getHorsepower() { return horsepower; }

    public int getHappiness() { return happiness; }

    public int getReliability() { return reliability; }

    public int getLoudness() { return loudness; }

    public int getCost() { return buildCost; }

    public String getTuner() { return tuner; }

    public int getBuildYear() { return buildYear; }

    public String getTrim() { return trim; }

    public int getBuildId() { return buildId; }

    // setters
    public void setTitle(String newTitle) { this.title = newTitle; }

    public void setHorsepower(int newHP) { this.horsepower = newHP; }

    public void setHappiness(int newHappy) {
        if (newHappy <= 10 && newHappy >= 0) { this.happiness = newHappy; } }

    public void setReliability(int newReliability) {
        if (newReliability <= 10 && newReliability >=0) { this.reliability = newReliability; } }

    public void setLoudness(int newLoudness) {
        if (newLoudness <= 10 && newLoudness >= 0) { this.loudness = newLoudness; } }

    public void setCost(int newCost) { if(newCost >=0) { this.buildCost = newCost; } }

    public void setTuner(String newTuner) { this.tuner = newTuner; }

    public void setBuildYear(int newYear) { if(newYear >= 2022) { this.buildYear = newYear; } }

    public void setTrim(String newTrim) { this.trim = newTrim; }

    public void setBuildId(int newBuildID) { this.buildId = newBuildID; }

    public void setOwnerId(int newId) { this.ownerID = newId; }

}
