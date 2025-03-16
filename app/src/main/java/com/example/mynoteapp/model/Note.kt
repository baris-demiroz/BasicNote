package com.example.mynoteapp.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Note (

    @ColumnInfo(name = "title")//isimler farklı da olabilir
    var title : String,

    @ColumnInfo(name = "note") //isimler farklı da olabilir
    var note : String,

    @ColumnInfo(name = "date")//isimler farklı da olabilir
    var date : Long

){
    @PrimaryKey(autoGenerate = true)
    var id =0
}