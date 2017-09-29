package com.jjh.parkinseoul.vo.response;

import com.jjh.parkinseoul.vo.ParkImageVO;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

import java.util.ArrayList;

/**
 * Created by JJH on 2016-08-28.
 */
public class ParkImageResponseData{
    @Element(name="title")
    private String title;
    @Element(name="link")
    private String link;
    @Element(name="description")
    private String description;
    @Element(name="lastBuildDate")
    private String lastBuildDate;
    @Element(name="total")
    private String total;
    @Element(name="start")
    private String start;
    @Element(name="display")
    private String display;
    @ElementList(entry="item", inline=true)
    private ArrayList<ParkImageVO> item;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLastBuildDate() {
        return lastBuildDate;
    }

    public void setLastBuildDate(String lastBuildDate) {
        this.lastBuildDate = lastBuildDate;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public ArrayList<ParkImageVO> getItem() {
        return item;
    }

    public void setItem(ArrayList<ParkImageVO> item) {
        this.item = item;
    }
}