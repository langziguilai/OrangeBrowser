console.log("tools JS has been injected");
function getHtml(){
  // return JSON.stringify({"content":'<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>'});
  return '<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>';
}
//获取所有的图片信息
function getImages(){
}
//获取所有的视频信息
function getVideos(){
}
//获取所有的脚本信息
function getJavaScripts(){
}
//获取所有的样式信息
function getStyles(){
}
