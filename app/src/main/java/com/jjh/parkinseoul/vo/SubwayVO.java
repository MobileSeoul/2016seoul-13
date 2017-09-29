package com.jjh.parkinseoul.vo;

/**
 * Created by JJH on 2016-09-10.
 */
public class SubwayVO {
    String statnId; //지하철역ID
    String statnNm; //지하철역명
    String subwayId; //지하철호선ID
    String subwayNm; //지하철호선명
    String ord; //근접순위
    String subwayXcnts; //지하철X좌표
    String subwayYcnts; //지하철Y좌표
    String imageX; //이미지상X좌표
    String imageY; //이미지상Y좌표

    public String getStatnId() {
        return statnId;
    }

    public void setStatnId(String statnId) {
        this.statnId = statnId;
    }

    public String getStatnNm() {
        return statnNm;
    }

    public void setStatnNm(String statnNm) {
        this.statnNm = statnNm;
    }

    public String getSubwayId() {
        return subwayId;
    }

    public void setSubwayId(String subwayId) {
        this.subwayId = subwayId;
    }

    public String getSubwayNm() {
        return subwayNm;
    }

    public void setSubwayNm(String subwayNm) {
        this.subwayNm = subwayNm;
    }

    public String getOrd() {
        return ord;
    }

    public void setOrd(String ord) {
        this.ord = ord;
    }

    public String getSubwayXcnts() {
        return subwayXcnts;
    }

    public void setSubwayXcnts(String subwayXcnts) {
        this.subwayXcnts = subwayXcnts;
    }

    public String getSubwayYcnts() {
        return subwayYcnts;
    }

    public void setSubwayYcnts(String subwayYcnts) {
        this.subwayYcnts = subwayYcnts;
    }

    public String getImageX() {
        return imageX;
    }

    public void setImageX(String imageX) {
        this.imageX = imageX;
    }

    public String getImageY() {
        return imageY;
    }

    public void setImageY(String imageY) {
        this.imageY = imageY;
    }
}
