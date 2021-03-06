package com.sd.src.stepcounterapp.dialog

import android.content.Context
import android.os.Build
import android.view.Gravity
import androidx.annotation.RequiresApi
import com.sd.src.stepcounterapp.changeDateFormat
import com.sd.src.stepcounterapp.getDaysDifference
import com.sd.src.stepcounterapp.interfaces.InterfacesCall
import com.sd.src.stepcounterapp.model.challenge.Data
import com.sd.src.stepcounterapp.model.challenge.Ongoing
import kotlinx.android.synthetic.main.dialog_stop_challenges.*

class StopChallengeDialog(
    context: Context, themeResId: Int,
    private val LayoutId: Int, data: Data,
    var mListener: StopInterface)
    : BaseDialog(context, themeResId) {
    var mData: Data = data
    init {
        val wmlp = this.window!!.attributes
        wmlp.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        window!!.attributes = wmlp
    }

    override fun getInterfaceInstance(): InterfacesCall.IndexClick {
        return this
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateStuff() {
        setData()
        btnYes.setOnClickListener {
            mListener.onStop(mData)
        }
    }

    override fun getContentView(): Int {
        return LayoutId
    }

    fun setData() {
        txtChallengeNameDetail.text = mData.name
        var daysDuration:String? = getDaysDifference(changeDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", "dd/MM/yyyy", mData.startDateTime), changeDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", "dd/MM/yyyy", mData.endDateTime)).toString()
        if(daysDuration.equals("0")){
            txtChallengeStopDateDialog.text = "Today"
        }else{
            txtChallengeStopDateDialog.text = "$daysDuration Days"
        }

        if(mData.is_completed){
            challengeMsgTxt.text = "Congratulations, your challenge has been completed."
        }else{
            challengeMsgTxt.text ="Do you want to stop the challenge? You will lose the progress."
        }
        btnNo.setOnClickListener {
            dismiss()
        }
    }

    fun dismissDialog(){
        dismiss()
    }


    interface StopInterface{
        fun onStop(data: Data)
    }
    override fun clickIndex(pos: Int) {
        dismiss()
    }
}