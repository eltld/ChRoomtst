package unicorn.ertech.chroom;

import java.io.Serializable;

/**
 * Created by Ильнур on 28.01.2015.
 */
public class conversationsMsg implements Serializable {
    String message;
    String uid;
    String from;
    String picURL;
    String direction;
    String msgId;
    String time;

    public conversationsMsg(String UID, String FROM, String MSG, String pcURL, String DIR, String mID, String TIME)
    {
        this.uid = UID;
        this.from = FROM;
        this.message = MSG;
        this.picURL = pcURL;
        this.direction = DIR;
        this.msgId = mID;
        this.time = TIME;
    }

}
