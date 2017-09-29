package com.jjh.parkinseoul.vo;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by JJH on 2016-08-21.
 */
@DatabaseTable(tableName = "favorite")
public class FavoriteVO implements Serializable{
    @DatabaseField(unique = true, canBeNull = false)
    private int parkVO_id;

    @DatabaseField(foreign = true, foreignAutoRefresh = true/*, foreignColumnName = "p_idx", columnName = "p_idx"*/)
    private ParkVO parkVO;

    public int getParkVO_id() {
        return parkVO_id;
    }

    public void setParkVO_id(int parkVO_id) {
        this.parkVO_id = parkVO_id;
    }

    public ParkVO getParkVO() {
        return parkVO;
    }

    public void setParkVO(ParkVO parkVO){
        this.parkVO = parkVO;
    }

}
