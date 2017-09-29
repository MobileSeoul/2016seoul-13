package com.jjh.parkinseoul.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.jjh.parkinseoul.common.Config;
import com.jjh.parkinseoul.vo.FavoriteVO;
import com.jjh.parkinseoul.vo.ParkMapItemVO;
import com.jjh.parkinseoul.vo.ParkVO;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JJH on 2016-09-04.
 */
public class DataBaseHelper extends OrmLiteSqliteOpenHelper {

    public static final String DB_FILE_NAME = "parkInSeoulDB.db";
    public static final String DB_FILE_PATH = "/data/data/" + Config.PACKAGE_NAME + "/databases/" + DB_FILE_NAME;
    private static final int DATABASE_VERSION = 20161023;

    private Context mContext;
    private Dao<ParkVO,Integer> mParkDao;
    private Dao<ParkMapItemVO,Integer> mParkMapItemDao;
    private Dao<FavoriteVO,Integer> mFavoriteDao;
    private SQLiteDatabase db;

    public DataBaseHelper(Context context) {
        super(context, DB_FILE_PATH, null, DATABASE_VERSION);
        mContext = context;
    }


    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
    }

    public void checkDB() {
        if (!isCheckDBFile()) {
            copyDatabaseFile();
        }
        //checkServerDBVersion();
    }

    // DB파일이 있나 체크하기
    public boolean isCheckDBFile() {
        try {
            if (checkDBFileExists()){//&& checkAppDBVersion()) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * DB파일 존재여부
     * @return
     */
    private boolean checkDBFileExists() {
        File file = new File(DB_FILE_PATH);
        return file.exists();
    }

    private void copyDatabaseFile() {
        // variables
        InputStream myInput = null;
        OutputStream myOutput = null;

        /*// get the database
        db = this.getReadableDatabase();
        ArrayList<BusVO> busBookmarkExistData = null;
        ArrayList<StationVO> stationBookmarkExistData = null;
        if (!checkAppDBVersion()) {
            // DB버전을 업데이트 하는 경우 저장되 있던 북마크 데이터 추출
            busBookmarkExistData = (ArrayList<BusVO>) selectList(R.string.select_bus_bookmark, new String[] {}, BusVO.class);
            stationBookmarkExistData = (ArrayList<StationVO>) selectList(R.string.select_station_bookmark, new String[] {}, StationVO.class);
        }
*/
        try {
            // Open your local db as the input stream
            myInput = mContext.getAssets().open(DB_FILE_NAME);

            File file = new File(DB_FILE_PATH.replace(DB_FILE_NAME, ""));
            if(!file.exists()){
                file.mkdirs();
            }
            // Open the empty db as the output stream
            myOutput = new FileOutputStream(new File(DB_FILE_PATH));

            // transfer bytes from the input file to the output file
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }

         /*   if (!checkAppDBVersion()) { // DB가 업데이트 된 경우  저장되 있던 북마크 데이터 다시 insert
                insertExistBookmarkData(busBookmarkExistData, stationBookmarkExistData);
            }*/

//            SharedPrefUtil.setSharedDBVersion(mContext, DB_VERSION);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // Close the streams
                myOutput.flush();
                myOutput.close();
                myInput.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * w즐겨찾는 공원 조회
     */
    public List<FavoriteVO> selectFavoriteParkList(){
        if (mParkDao == null) {
            try {
                mParkDao = getDao(ParkVO.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        if(mFavoriteDao == null){
            try{
                mFavoriteDao = getDao(FavoriteVO.class);
            } catch (SQLException e){
                e.printStackTrace();
            }
        }
        QueryBuilder<FavoriteVO, Integer> qbFavorite = mFavoriteDao.queryBuilder();
        QueryBuilder<ParkVO, Integer> qbPark = mParkDao.queryBuilder();
        try {
            return qbFavorite.join(qbPark).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new ArrayList<FavoriteVO>();
    }

    public boolean isExistsFavorite(int pIdx){
        if (mFavoriteDao == null) {
            try {
                mFavoriteDao = getDao(FavoriteVO.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        QueryBuilder<FavoriteVO, Integer> qb = mFavoriteDao.queryBuilder();
        try {
            return qb.where().eq("parkVO_id",pIdx).query().size() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 즐겨찾는 공원 추가
     */
    public boolean insertFavoritePark(FavoriteVO vo){
        if(mFavoriteDao == null){
            try{
                mFavoriteDao = getDao(FavoriteVO.class);
            } catch (SQLException e){
                e.printStackTrace();
            }
        }
        int resultCnt = 0;
        try {
            resultCnt = mFavoriteDao.create(vo);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return resultCnt > 0;
    }

    /**
     * 즐겨찾는 공원 삭제
     */
    public boolean deleteFavoritePark(int pIdx){
        if(mFavoriteDao == null){
            try{
                mFavoriteDao = getDao(FavoriteVO.class);
            } catch (SQLException e){
                e.printStackTrace();
            }
        }
        int resultCnt = 0;
        try {
            DeleteBuilder db = mFavoriteDao.deleteBuilder();
            db.where().eq("parkVO_id",pIdx);
            resultCnt = mFavoriteDao.delete(db.prepare());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return resultCnt > 0;
    }


    /**
     * 공원명으로 공원 조회
     * @param name 공원명
     * @return
     */
    public List<ParkVO> selectParkListByName(String name){
        if (mParkDao == null) {
            try {
                mParkDao = getDao(ParkVO.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        QueryBuilder<ParkVO, Integer> qb = mParkDao.queryBuilder();
        try {
            return qb.where().like("p_park","%" + name + "%").query();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new ArrayList<ParkVO>();
    }

    /**
     * 주소로 공원 목록 조회
     * @param addr 주소
     * @return
     */
    public List<ParkVO> selectParkListByAddr(String addr){
        if (mParkDao == null) {
            try {
                mParkDao = getDao(ParkVO.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        QueryBuilder<ParkVO, Integer> qb = mParkDao.queryBuilder();
        try {
            return qb.where().like("p_addr","%" + addr + "%").query();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new ArrayList<ParkVO>();
    }

    /**
     * 내주변 공원 조회
     * @return
     */
    public List<ParkMapItemVO> selectParkMapItemList(){
        if (mParkMapItemDao == null) {
            try {
                mParkMapItemDao = getDao(ParkMapItemVO.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        QueryBuilder<ParkMapItemVO, Integer> qb = mParkMapItemDao.queryBuilder();
        try {
            return qb.query();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new ArrayList<ParkMapItemVO>();
    }

    /**
     * 공원 상세 정보 조회
     * @param pIdx 공원번호
     * @return
     */
    public ParkVO selectParkDetail(int pIdx){
        if (mParkDao == null) {
            try {
                mParkDao = getDao(ParkVO.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        QueryBuilder<ParkVO, Integer> qb = mParkDao.queryBuilder();
        try {
            List<ParkVO> result = qb.where().eq("p_idx",pIdx).query();
            if(result.size() > 0){
                return result.get(0);
            }else{
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static int getDatabaseVersion(){
        return DATABASE_VERSION;
    }
}