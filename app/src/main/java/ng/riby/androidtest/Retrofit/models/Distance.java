package ng.riby.androidtest.Retrofit.models;

/**
 * Created by Manuel Chris-Ogar on 6/1/2019.
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Distance {

    @SerializedName("value")
    @Expose
    private Integer value;
    @SerializedName("text")
    @Expose
    private String text;

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
