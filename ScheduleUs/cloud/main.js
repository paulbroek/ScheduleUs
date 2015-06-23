
// Use Parse.Cloud.define to define as many cloud functions as you want.
// For example:
Parse.Cloud.define("hello", function(request, response) {
  response.success("Hello world!");
});

/* Parse.Cloud.afterSave("AvailItems", function(request) {
    query = new Parse.Query("_User");
    query.get(request.object.get("User").id, {
    success: function(user) {
      user.increment("no_Items");
      user.save();
    },
    error: function(error) {
      console.error("Got an error " + error.code + " : " + error.message);
    }
  });
});  */ 

 Parse.Cloud.afterSave("AvailItems", function(request) {
    /* arr2 =  "{{10,0,11,0}}";
    var arr = JSON.parse(arr2); */
    
    var string = [[10,0,11,0]];
    var list = [];
    // Get all availability items of the selected parent event
    query1 = new Parse.Query("AvailItems");
    query1.equalTo("parent_event", request.object.get("parent_event").id);
    query1.find({
        succes: function(results) {
            for (var i = 0; i < results.length; i++) {
                list += results[i].get("Times");
            }
        },
        error: function(error) {
            console.error("Got an error " + error.code + " : " + error.message);
        }
    });
  
    /* query2 = new Parse.Query("SharedTimes");
    query2.get(request.object.get("SharedTime").id, {
    success: function(sharedtime) {
        
        sharedtime.set("Times", string);
        sharedtime.set("Day", "what");
        sharedtime.save();
    },
    error: function(error) {
        console.error("Got an error " + error.code + " : " + error.message);
    }
  }); */
  
}); 
