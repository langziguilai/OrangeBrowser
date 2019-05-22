console.log("inject read mode js");
var SMALL_FONT_SIZE="16px"
var MEDIUM_FONT_SIZE="18px"
var LARGE_FONT_SIZE="20px"
function setFontSize(size){
  switch(size){
     case 1:{
        document.querySelector("html").style.fontSize=SMALL_FONT_SIZE
        break;
     }
     case 2:{
        document.querySelector("html").style.fontSize=MEDIUM_FONT_SIZE
        break;
     }
     case 3:{
        document.querySelector("html").style.fontSize=LARGE_FONT_SIZE
        break;
     }
  }
}
//设置中号字大小
setFontSize(2)
var BG_WHITE="#FFFFFF";
var TEXT_COLOR_FOR_BG_WHITE="#1B1B1B";
var BG_MO="#F8F1E3";
var TEXT_COLOR_FOR_BG_MO="#4F321C";
var BG_GERY="#5A5A5C";
var TEXT_COLOR_FOR_BG_GERY="#CBCBCB";
var BG_BLACK="#121212";
var TEXT_COLOR_FOR_BG_BLACK="#B0B0B0";
function setStyle(style){
     switch(style){
        case 1:{
           document.querySelector("body").style.backgroundColor=BG_WHITE
           document.querySelector("body").style.color=TEXT_COLOR_FOR_BG_WHITE
           break;
        }
        case 2:{
           document.querySelector("body").style.backgroundColor=BG_MO
           document.querySelector("body").style.color=TEXT_COLOR_FOR_BG_MO
           break;
        }
        case 3:{
           document.querySelector("body").style.backgroundColor=BG_GERY
           document.querySelector("body").style.color=TEXT_COLOR_FOR_BG_GERY
           break;
        }
        case 3:{
           document.querySelector("body").style.backgroundColor=BG_BLACK
           document.querySelector("body").style.color=TEXT_COLOR_FOR_BG_BLACK
           break;
        }
     }
}

setStyle(3)