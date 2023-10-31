package com.example.calcapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.calcapp.components.InputField
import com.example.calcapp.ui.theme.CalcAppTheme
import com.example.calcapp.utils.calculateTotalPerPerson
import com.example.calcapp.utils.calculateTotalTip
import com.example.calcapp.widgets.RoundIconButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp {
//                TopHeader()
                MainContent()
            }
        }
    }
}


@Composable
fun MyApp(content:@Composable () -> Unit) {
    CalcAppTheme {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
           content()
        }
    }
}

//@Preview
@Composable
fun TopHeader(totalPerPerson:Double=0.0) {
    Surface(

        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .height(150.dp)
            .clip(shape = CircleShape.copy(all = CornerSize(12.dp))),
        color = Color(0xFFE9D7F7)
//            .clip(shape = RoundedCornerShape(corner = CornerSize(12.dp)))
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            val total = "%.2f".format(totalPerPerson)
            Text(text = "Total Per Person", style = MaterialTheme
                .typography.titleSmall)
            Text(text = "$$total", style = MaterialTheme
                .typography.titleLarge
                .copy(fontWeight = FontWeight.ExtraBold))
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Preview
@Composable
fun MainContent() {
    BillForm(){billAmount->
        Log.d("AMT", "MainContent: $billAmount")
    }


}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BillForm(modifier:Modifier=Modifier,
             onValChanged:(String) -> Unit={}) {

    val totalBillState= remember {
        mutableStateOf("")
    }
    val validState = remember(totalBillState.value) {
        totalBillState.value.trim().isNotEmpty()
    }

    val splitByState= remember {
        mutableIntStateOf(1)
    }

    val sliderPositionState = remember {
        mutableFloatStateOf(0f)
    }

    val tipPercentage = (sliderPositionState.value*100).toInt()
    val range = IntRange(start = 1, endInclusive = 100)

    val tipAmountState= remember {
        mutableStateOf(0.0)
    }

    val totalPerPersonState = remember {
        mutableDoubleStateOf(0.0)
    }

    val keyboardController =LocalSoftwareKeyboardController.current

    Surface(
        modifier = Modifier
            .padding(2.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(corner = CornerSize(8.dp)),
        border = BorderStroke(width = 1.dp, color = Color.LightGray)
    ) {
        Column(
            modifier=Modifier.padding(6.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
//13/01.53
            TopHeader(totalPerPerson = totalPerPersonState.value)
            InputField(valueState =totalBillState ,
                labelId = "Enter Bill",
                enabled =true,
                isSingleLine =true,
                onActions = KeyboardActions{
                    if(!validState)return@KeyboardActions
                    onValChanged(totalBillState.value.trim())
                    keyboardController?.hide()
                }
            )
            
//            if(validState){
                Row (
                    modifier=Modifier.padding(3.dp),
                    horizontalArrangement = Arrangement.Start
                ){
                    Text(text = "Split", modifier = Modifier.align(
                        alignment = Alignment.CenterVertically
                    ))

                    Spacer(modifier = Modifier.width(120.dp))
                    Row (
                        modifier=Modifier.padding(horizontal = 3.dp),
                        horizontalArrangement = Arrangement.End
                    ){
                        RoundIconButton(
                            imageVector = Icons.Default.Remove,
                            onClick = {
                                splitByState.value =
                                if (splitByState.value >1)
                                    splitByState.value-1
                                else 1
                                calculateTotalPerPerson(
                                    tipPercentage = tipPercentage,
                                    totalBill = totalBillState.value.toDouble(),
                                    splitBy = splitByState.value
                                )

                            }
                        )

                        Text(text = splitByState.value.toString(),
                            modifier = Modifier
                                .padding(start = 9.dp, end = 9.dp)
                                .align(Alignment.CenterVertically))

                        RoundIconButton(
                            imageVector = Icons.Default.Add,
                            onClick = {
                                if (splitByState.value < range.last){
                                    splitByState.value = splitByState.value+1
                                    calculateTotalPerPerson(
                                        tipPercentage = tipPercentage,
                                        totalBill = totalBillState.value.toDouble(),
                                        splitBy = splitByState.value
                                    )

                                }
                            }
                        )
                    }

                }

//            Tip Row
            Row(
                modifier=Modifier.padding(horizontal = 3.dp,
                    vertical = 12.dp)
            ){
                Text(text = "Tip",
                    modifier=Modifier
                        .align(alignment = Alignment.CenterVertically))
                Spacer(modifier = Modifier.width(200.dp))
                Text(text = "${tipAmountState.value}",modifier=Modifier.
                align(alignment = Alignment.CenterVertically))
            }

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "$tipPercentage %")
                Spacer(modifier = Modifier.height(14.dp))
                Slider(
                    modifier=Modifier.padding(start = 16.dp,end=16.dp),
                    steps =5 ,
                    value = sliderPositionState.value,
                    onValueChange ={newVal ->
                        sliderPositionState.value=newVal
                       tipAmountState.value=
                           calculateTotalTip(totalBill  =totalBillState.value.toDouble(),
                        tipPercentage=  tipPercentage)

                        totalPerPersonState.value =
                            calculateTotalPerPerson(
                                tipPercentage = tipPercentage,
                                totalBill = totalBillState.value.toDouble(),
                                splitBy = splitByState.value
                            )
                    } )
            }
//            }else{
//                Box {}
//            }
        }
    }
}
