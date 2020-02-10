module.exports = {


  friendlyName: 'View by category',


  description: 'Display "By category" page.',

  exits: {

    success: {
      viewTemplatePath: 'pages/by-category'
    }

  },


  fn: async function () {

        var rawFile = new XMLHttpRequest();
        rawFile.open("GET", "test.txt", false);
        rawFile.onreadystatechange = function ()
        {
            if(rawFile.readyState === 4)
            {
                if(rawFile.status === 200 || rawFile.status == 0)
                {
                    var allText = rawFile.responseText;
                    alert(allText);
                }
            }
        }
        rawFile.send(null);

        console.log(allText);

        this.location = {
            alltext
        }

    // Respond with view.
    return {Input: allText};
  }


};
