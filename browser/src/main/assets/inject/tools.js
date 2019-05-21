console.log("tools JS has been injected")
function getHtml(){
   return '<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');
}