package com.Yfun.interview.service.email;

import com.Yfun.interview.dao.LeaveTable;
import org.apache.commons.lang.StringUtils;

import javax.xml.crypto.Data;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @ClassName : EmailMessageExplain
 * @Description :
 * @Author : DeYuan
 * @Date: 2020-09-04 18:49
 */
public class EmailMessageExplain {
    public String htmlTemplate(LeaveTable leaveTable){
        String result="";
        URL url = this.getClass().getResource("/LeavetableTemplate.html");
        FileInputStream resource = null;
        try {
            resource = new FileInputStream(new File(url.toURI()));
        } catch (FileNotFoundException | URISyntaxException e) {
            e.printStackTrace();
        }
        FileChannel channelIn = resource.getChannel();
        File file = new File("/" + System.currentTimeMillis() + ".tmp");


        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            FileChannel  channelOut= fileOutputStream.getChannel();
            channelOut.transferFrom(channelIn,0,channelIn.size());
            ByteBuffer allocate = ByteBuffer.allocate((int) channelOut.size());
            channelOut.close();
            FileChannel channelN=new FileInputStream(file).getChannel();
            channelN.read(allocate);
             result = new String(allocate.array());
             channelN.close();
             if(StringUtils.isBlank(result)){
                 return "";
             }
             result=
             result.replace("%name%",leaveTable.getName()).
             replace("%nickname%",leaveTable.getNickname()).replace("%department%",leaveTable.getDepartment()).
             replace("value=\""+leaveTable.getLeaveType()+"\"","value=\""+leaveTable.getLeaveType()+"\""+" checked=\"checked\"");
            Map<String,String> map = timeParser(leaveTable.getLeaveDate());
           result=result.replace("%src_year%",map.get("src_year")).
            replace("%src_month%",map.get("src_month")).
           replace("%src_day%",map.get("src_day")).
          replace("%src_hours%",map.get("src_hours")).
            replace("%det_year%",map.get("det_year")).
          replace("%det_month%",map.get("det_month")).
           replace("%det_day%",map.get("det_day")).
            replace("%det_hours%",map.get("det_hours")).
            replace("%det_to_day%",map.get("det_to_day")).
          replace("%det_to_hours%",map.get("det_to_hours")).
            replace("%leave_reason%",leaveTable.getLeaveReason()).
           replace("%remarks%",leaveTable.getRemarks());
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                channelIn.close();
                file.delete();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
    private Map timeParser(String leaveDate){
        // "2020-09-01-13:14:15$$2020-08-04-13:14:15"
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
        String[] times = leaveDate.split("\\$\\$");
        HashMap<String, String> resultMap = new HashMap<>();
        String start_time = times[0];
        String end_time = times[1];
        try {
            Date asTime = format.parse(start_time);
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(asTime);
            String src_year= String.valueOf(calendar.get(Calendar.YEAR));
            String src_month = String.valueOf(calendar.get(Calendar.MONTH));
            String src_day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
            String src_hours = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
            Date toTime = format.parse(end_time);
            calendar = new GregorianCalendar();
            calendar.setTime(toTime);
            String det_year= String.valueOf(calendar.get(Calendar.YEAR));
            String det_month = String.valueOf(calendar.get(Calendar.MONTH));
            String det_day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
            String det_hours = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
            long timestemp = toTime.getTime() - asTime.getTime();
            String det_to_day= String.valueOf(timestemp/1000/(3600*24));
            String det_to_hours= String.valueOf((timestemp/1000/3600)%24);
            resultMap.put("src_year",src_year);
            resultMap.put("src_month",src_month);
            resultMap.put("src_day",src_day);
            resultMap.put("src_hours",src_hours);
            resultMap.put("det_year",det_year);
            resultMap.put("det_month",det_month);
            resultMap.put("det_day",det_day);
            resultMap.put("det_hours",det_hours);
            resultMap.put("det_to_day",det_to_day);
            resultMap.put("det_to_hours",det_to_hours);


        } catch (ParseException e) {
            e.printStackTrace();
        }
        return resultMap;
    }
}
