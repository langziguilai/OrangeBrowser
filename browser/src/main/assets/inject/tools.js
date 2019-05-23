console.log("tools JS has been injected");
function getHtml(){
  // return JSON.stringify({"content":'<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>'});
  return '<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>';
}
//获取所有的图片信息
function getImages(minWidth=100,minHeight=100){
    var result=[];
    var images=document.getElementsByTagName('img');
    for(var index in images){
        var image=images[index];
        if(image && image.naturalWidth>=minWidth && image.naturalHeight>=minHeight){
             var obj={
                  "width":image.naturalWidth,
                  "height":image.naturalHeight,
                  "src":getRealSrc(image)
             }
             result.push(obj)
        }
    }
    return JSON.stringify(result)
}

function getRealSrc(element){
    var src=element.getAttribute("src")
    if(!src.startsWith("data:")){
       return src
    }else{
       var attrs=image.attributes;
       for(var key in attrs){
           var value=attrs[key];
           if((typeof value)==="string"){
               var src=extractUrlFromString(value)
               if(src.length>0){
                  return src
               }
           }
       }
    }
    return ""
}
//TODO:获取真实的SRC
function extractUrlFromString(str){

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
