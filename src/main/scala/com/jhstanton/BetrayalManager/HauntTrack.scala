package com.jhstanton.BetrayalManager

import org.scaloid.common._
import android.os.Bundle
import android.content.Intent
import android.graphics.Color
import android.view.View
import android.view.ViewGroup.{LayoutParams}
import android.widget.LinearLayout
import android.content.SharedPreferences
import scala.util.Random

object HauntTrack extends SActivity {
  def apply()       = new HauntTrack()
  val HAUNT_PACKAGE = "com.jhstanton.BetrayalManager.HauntTrack"
  val HAUNT_INDEX   = HAUNT_PACKAGE + "haunt_index"
}

class HauntTrack extends SActivity {
  val numDice    : Int = 8 // 8d6, 
  var hauntLvl   : Int = 1
  val center           =  0x11
  val hauntLvlBase     = "Haunt Level: "
  val hauntLvlSize     = 140
  val rollBaseStr      = "Last Roll: "

  val params = new LinearLayout.LayoutParams( LayoutParams.MATCH_PARENT
					      , LayoutParams.MATCH_PARENT
					      , 1.0f)

  override def onCreate(savedInstanceState : Bundle) {
    super.onCreate(savedInstanceState)

    val prefs = getPreferences(0)
    if (savedInstanceState == null && prefs == null)
      hauntLvl = 0
    else if(prefs == null)
      hauntLvl = savedInstanceState.getInt(HauntTrack.HAUNT_INDEX)
    else
      hauntLvl = prefs.getInt(HauntTrack.HAUNT_INDEX, 0)
    val scrollEnvironment = new SScrollView()
    val mainLayout = new SVerticalLayout()
    
    val hauntLvlView = new STextView(hauntLvl.toString).textSize(hauntLvlSize sp).gravity(center)
    hauntLvlView.setTextColor(Color.RED)
    mainLayout += hauntLvlView
    val modButtons = new SLinearLayout{
      SButton("+", update( 1, hauntLvlView) _).<<.Weight(1.0f).>>
      SButton("-", update(-1, hauntLvlView) _).<<.Weight(1.0f).>> 
    }
    mainLayout += modButtons
    val reset = new SButton("Reset", update(1 - hauntLvl, hauntLvlView) _)
    mainLayout += reset
    
    val rollTextView  = new STextView(rollBaseStr + "0").textSize((hauntLvlSize / 4) sp).gravity(center)
    rollTextView.setTextColor(Color.RED)
    val rollBtn       = new SButton("Roll", diceHandler(rollTextView, hauntLvlView) _)
    mainLayout += rollTextView
    mainLayout += rollBtn

    scrollEnvironment += mainLayout
    setContentView(scrollEnvironment)
  }

  def update(modifier : => Int, textView: STextView)(view: View) {
    val newIndex = this.hauntLvl + modifier
    if (newIndex >= 1 && newIndex <= 10) {
      this.hauntLvl = newIndex
      textView.setText(hauntLvl.toString)
    }    
  }

  override def onSaveInstanceState(savedInstanceState: Bundle) {
    savedInstanceState.putInt(HauntTrack.HAUNT_INDEX, hauntLvl)
    super.onSaveInstanceState(savedInstanceState)
  }

  override def onBackPressed() {
    val editor : SharedPreferences.Editor = getPreferences(0).edit()
    editor.putInt(HauntTrack.HAUNT_INDEX, hauntLvl)
    editor.commit
    super.onBackPressed
  }

  def diceHandler(rollDisplay: STextView, hauntLvlView: STextView)(view: View) {
    val rollResult = rollDice
    rollDisplay.setText(rollBaseStr + rollResult.toString)
    if (rollResult <= hauntLvl) {
      // Start the haunt activity
      alert("Your Haunt Begins", "MUAHAHAHAHA")
      val intent : Intent = new Intent(this, classOf[HauntActivity])
      startActivity(intent)
    }
    else {
      update(1, hauntLvlView)(null)
    }
     
  }
  def rollDice(): Int = { 
    (1 to numDice).map{ _ => 
      val roll = Random.nextDouble
      if(roll < .33)
	0
      else if(roll < .66)
        1
      else
	2
    }.sum
  }
  
}

