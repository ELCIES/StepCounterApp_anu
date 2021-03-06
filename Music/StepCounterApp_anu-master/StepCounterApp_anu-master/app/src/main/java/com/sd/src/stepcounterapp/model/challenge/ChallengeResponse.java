package com.sd.src.stepcounterapp.model.challenge;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;
/**
 * Awesome Pojo Generator
 * */
public class ChallengeResponse{
    public List<Data> getTrending() {
        return trending;
    }
    
    public void setTrending(List<Data> trending) {
        this.trending = trending;
    }
    
    public List<Data> getFeatured() {
        return featured;
    }
    
    public void setFeatured(List<Data> featured) {
        this.featured = featured;
    }
    
    public List<Ongoing> getOngoing() {
        return ongoing;
    }
    
    public void setOngoing(List<Ongoing> ongoing) {
        this.ongoing = ongoing;
    }
    
    @SerializedName("trending")
  @Expose
  private List<Data> trending;
  @SerializedName("featured")
  @Expose
  private List<Data> featured;
  @SerializedName("ongoing")
  @Expose
  private List<Ongoing> ongoing;
  @SerializedName("data")
  @Expose
  private List<Data> data;
  @SerializedName("message")
  @Expose
  private String message;
  @SerializedName("status")
  @Expose
  private Integer status;
    @SerializedName("count")
    @Expose
    private Integer count;

    public void setData(List<Data> data){
   this.data=data;
  }
  public List<Data> getData(){
   return data;
  }
  public void setMessage(String message){
   this.message=message;
  }
  public String getMessage(){
   return message;
  }
  public void setStatus(Integer status){
   this.status=status;
  }
  public Integer getStatus(){
   return status;
  }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}