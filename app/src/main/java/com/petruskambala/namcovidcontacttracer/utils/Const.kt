package com.petruskambala.namcovidcontacttracer.utils

internal interface Const {
    companion object {
        val MODEL_POS: String = "model_pos"
        val CASE: String = "case"
        val PASSWORD: String = "password"

        //constant for activity results
        val RC_SIGN_IN = 0
        val CAPTURE_PICTURE = 1
        val SCAN_BARCODE = 2


        //Transformation constants
        val CIRCLE = "Circle"
        val SQUARE = "Square"
        val ICON_PATH = "ICON_PATH"
        val ACCOUNT = "business"
    }

}