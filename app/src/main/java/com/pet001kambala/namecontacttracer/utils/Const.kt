package com.pet001kambala.namecontacttracer.utils

internal interface Const {
    companion object {
        val PERSON: String = "person"
        val PHONE_NUMBER: String = "phone number"
        val PERSON_ID: String = "personId"
        val MODEL_POS: String = "model_pos"
        val CASE: String = "case"
        val PASSWORD: String = "password"

        //constant for activity results
        val RC_SIGN_IN = 0
        val CAPTURE_PICTURE = 1
        val SCAN_BARCODE = 2

        val IMAGE_ROOT_PATH = "images"


        //Transformation constants
        val CIRCLE = "Circle"
        val SQUARE = "Square"
        val ICON_PATH = "ICON_PATH"
        val ACCOUNT = "business"
        val AUTH_TYPE = "auth_type"

        val TEMP_FILE = "temp"//only used when creating a new model


        val DISCARD_CHANGES = "Discard Changes?"
    }

}