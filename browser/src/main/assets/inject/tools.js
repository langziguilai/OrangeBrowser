console.log("tools JS has been injected");
function getHtml(){
  return '<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>';
}
//get all images information
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
//extract url from text
function extractUrlFromString(str){

}

//get all videos information
function getVideos(){
}
//get all javascript information
function getJavaScripts(){
}
//get all stylesheet information
function getStyles(){
}