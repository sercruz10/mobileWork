package com.nodomain.freeyourgadget.gadgetbridge.pm.objects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ConsumeApi {
    @SerializedName("coord")
    @Expose
    private Api.Coord coord;
    @SerializedName("weather")
    @Expose
    private List<Api.Weather> weather = null;
    @SerializedName("base")
    @Expose
    private String base;
    @SerializedName("main")
    @Expose
    private Api.Main main;
    @SerializedName("visibility")
    @Expose
    private Integer visibility;
    @SerializedName("wind")
    @Expose
    private Api.Wind wind;
    @SerializedName("clouds")
    @Expose
    private Api.Clouds clouds;
    @SerializedName("dt")
    @Expose
    private Integer dt;
    @SerializedName("sys")
    @Expose
    private Api.Sys sys;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("cod")
    @Expose
    private Integer cod;

    public Api.Coord getCoord() {
        return coord;
    }

    public void setCoord(Api.Coord coord) {
        this.coord = coord;
    }

    public List<Api.Weather> getWeather() {
        return weather;
    }

    public void setWeather(List<Api.Weather> weather) {
        this.weather = weather;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public Api.Main getMain() {
        return main;
    }

    public void setMain(Api.Main main) {
        this.main = main;
    }

    public Integer getVisibility() {
        return visibility;
    }

    public void setVisibility(Integer visibility) {
        this.visibility = visibility;
    }

    public Api.Wind getWind() {
        return wind;
    }

    public void setWind(Api.Wind wind) {
        this.wind = wind;
    }

    public Api.Clouds getClouds() {
        return clouds;
    }

    public void setClouds(Api.Clouds clouds) {
        this.clouds = clouds;
    }

    public Integer getDt() {
        return dt;
    }

    public void setDt(Integer dt) {
        this.dt = dt;
    }

    public Api.Sys getSys() {
        return sys;
    }

    public void setSys(Api.Sys sys) {
        this.sys = sys;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCod() {
        return cod;
    }

    public void setCod(Integer cod) {
        this.cod = cod;
    }
}
