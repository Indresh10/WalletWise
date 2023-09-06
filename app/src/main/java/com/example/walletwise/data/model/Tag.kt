package com.example.walletwise.data.model


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.FoodBank
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.House
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.SportsBaseball
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tag")
data class Tag(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val name: String,
    val type: Boolean,
    val icon: Int
)

enum class TagType(val type: Boolean) {
    Income(true), Expense(false)
}

enum class ImageType(val res: ImageVector) {
    Bill(Icons.Filled.ReceiptLong), Cloth(Icons.Filled.Checkroom), Education(Icons.Filled.HistoryEdu),
    Entertainment(Icons.Filled.LiveTv), Gifts(Icons.Filled.CardGiftcard), Food(Icons.Filled.FoodBank),
    Health(Icons.Filled.LocalHospital), Travel(Icons.Filled.TravelExplore), Person(
        Icons.Filled.Person
    ),
    House(Icons.Filled.House), Shopping(Icons.Filled.ShoppingBag), Sports(Icons.Filled.SportsBaseball),
    Other(Icons.Filled.Category), Salary(Icons.Filled.Payments)
}
