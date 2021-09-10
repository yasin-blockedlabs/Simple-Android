package com.blockedlabs.custompattern.utills

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import com.blockedlabs.custompattern.activities.SuperActivity

class PermissionUtills {
    
    companion object{
        
        @RequiresApi(Build.VERSION_CODES.M)
        fun requestPermission(activity: Activity,
                              requestCode: Int,
                              vararg permissions: String){
            activity.requestPermissions(permissions, requestCode)
        }

        fun permissionResult(requestCode: Int,
                             permissions: Array<out String>,
                             grantResults: IntArray, permissionResults: HashMap<Int, (permissionGranted: Boolean) -> Unit>){
            val result = permissionResults[requestCode]
            result.let {
                var result = true
                for (grantResult in grantResults){
                    if(grantResult != PackageManager.PERMISSION_GRANTED) {
                        result = false
                    }
                }
                permissionResults.remove(requestCode)
                it?.let { it(result) }
            }
        }


        fun imagePermission(activity: SuperActivity, action: () -> Unit){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                activity.requestPermission(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA){
                    if(it){
                        action()
                    }else{
                        activity.toast("Need Camera and Storage Permission!")
                    }
                }
            }else{
                action()
            }
        }



        /*fun imagePermission(fragment: SuperFragment, action: () -> Unit){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                activity.requestPermission(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA){
                    if(it){
                        action()
                    }else{
                        activity.toast("Need Camera and Storage Permission!")
                    }
                }
            }else{
                action()
            }
        }*/
    }
    
}