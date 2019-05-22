console.log("inject read mode js");
function setFontSize(size){
 document.querySelector("html").style.fontSize=size+"px";
}
//设置中号字大小
setFontSize(18)

function setStyle(backgroundColor,textColor){
   document.querySelector("body").style.backgroundColor=backgroundColor;
   document.querySelector("body").style.color=textColor;
}