package com.petruskambala.namcovidcontacttracer.utils

internal interface Const {
    companion object {
        val MODEL: String = "model"

        //constant for activity results
        val RC_SIGN_IN = 0
        val CAPTURE_PICTURE = 1
        val SCAN_BARCODE = 2


        //Transformation constants
        val CIRCLE = "Circle"
        val SQUARE = "Square"
        val ICON_PATH = "ICON_PATH"

        //image paths on device
        val IMAGE_ROOT_PATH = "images"
        val TEMP_FILE = "temp"//only used when creating a new model
        val BUSINESS_IMAGES = ParseUtil.path(IMAGE_ROOT_PATH, "business")
        val PRODUCT_IMAGES = ParseUtil.path(IMAGE_ROOT_PATH, "products")
        val USER_IMAGES = ParseUtil.path(IMAGE_ROOT_PATH, "users")
        val MODEL_ICON_NAME = "model_icon"
        val BASEDIR = "basedir"
        val FILE_NAME = "filename"

        //constant for passing models around fragments
        val BUSINESS = "BUSINESS"
        val PRODUCT = "PRODUCT"
        val USER = "USER"


        val BARCODE = "Barcode"

        val ITEM_POS = "item_pos"
        val MIN_QTY = 1
        val ACTIVITY_CODE = "new_stock"
        val NEW_STOCK_FRAGMENT = 1 shl 1
        val EMPLOYEE = "employee"
        val UPDATE_SUCCESS = 1 shl 2
        val UPDATE_MSG = "update_msg"
        val UPDATE_FAIL = 1 shl 3
        val MODEL_POS = "model_pos"
        val STOCK = "stock"
        val DEL_MODEL = "Delete this "
        val DISCARD_CHANGES = "Discard Changes?"
    }

}