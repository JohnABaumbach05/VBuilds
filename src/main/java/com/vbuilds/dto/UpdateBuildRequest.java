package com.vbuilds.dto;


public class UpdateBuildRequest {
    private String title;
    private Integer buildYear;
    private String trim;
    private Integer horsepower;
    private String tuner;
    private Integer cost;
    private String description;
    private Integer happiness;
    private Integer reliability;
    private Integer loudness;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Integer getBuildYear() { return buildYear; }
    public void setBuildYear(Integer buildYear) { this.buildYear = buildYear; }
    public String getTrim() { return trim; }
    public void setTrim(String trim) { this.trim = trim; }
    public Integer getHorsepower() { return horsepower; }
    public void setHorsepower(Integer horsepower) { this.horsepower = horsepower; }
    public String getTuner() { return tuner; }
    public void setTuner(String tuner) { this.tuner = tuner; }
    public Integer getCost() { return cost; }
    public void setCost(Integer cost) { this.cost = cost; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getHappiness() { return happiness; }
    public void setHappiness(Integer happiness) { this.happiness = happiness; }
    public Integer getReliability() { return reliability; }
    public void setReliability(Integer reliability) { this.reliability = reliability; }
    public Integer getLoudness() { return loudness; }
    public void setLoudness(Integer loudness) { this.loudness = loudness; }
}
