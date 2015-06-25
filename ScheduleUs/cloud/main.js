// Cloud code functions are executed when a server request enters

// Delete all previously entered items of this event, user and day
Parse.Cloud.beforeSave("AvailItems", function(request, response) {
   
    var query = new Parse.Query("AvailItems");
    query.equalTo("User", request.object.get("User"));
    query.equalTo("parent_event", request.object.get("parent_event"));
    query.equalTo("Day", request.object.get("Day"));
    query.find({
        success: function(result) {
            console.log(result.length);
            Parse.Object.destroyAll(result, {
                success: function() {
                    console.log("destroyed");
                },
                error: function() {
                    console.log("destroy error");
                }
            })
            response.success();
            console.log("Deleted: " + result.length);
        },
        error: function(e) {
            console.log('err');
            console.log(e);
            response.error('no objects found');
        }
    });
}); 

// Merge the AvailItems of all participants of a day of an event
 Parse.Cloud.afterSave("AvailItems", function(request) {
    console.log("after save");    
            
    // Get all AvailItems for this day and event
    var query2 = new Parse.Query("AvailItems");
    query2.equalTo("parent_event", request.object.get("parent_event"));
    query2.equalTo("Day", request.object.get("Day"));
    query2.find({
        // We did find some items
        success: function(result) {
            var shared_time = [];
            var shared_time_personal = [[]];
            var minute = 0;
            var hour_later = 0;
            var minute_later = 0;
            
            // Create 60 empty items for every quarter between 9 and 24 hours
            for (var k = 0; k < 60; k++) {
                var hour = 9 + k/4 - ((k/4)%1);   
                hour_later = hour;
                minute = (k%4)*15;
                minute_later = minute + 15;
                
                if (minute == 45) {
                    minute_later = 0;
                    hour_later = hour+1;
                }
                shared_time[k] = [hour,minute,hour_later,minute_later,false];
             
            }
            minute = 0;
            hour_later = 0;
            minute_later = 0;
            // Create the same list of items, for every user a unique one
            for (var user = 0; user < result.length; user++) {
                var temp = [];
                for (var k = 0; k < 60; k++) {
                    var hour = 9 + k/4 - ((k/4)%1);   
                    hour_later = hour;
                    minute = (k%4)*15;
                    minute_later = minute + 15;
                    
                    if (minute == 45) {
                        minute_later = 0;
                        hour_later = hour+1;
                    }
                    temp[k] = [hour,minute,hour_later,minute_later,false];
                
                }
                shared_time_personal[user] = temp;
            } 
            
            function time_to_int(p1,p2) {
                return ((p1-9)*4 + p2/15);
            }
         
            // Iterate through users
            for (var i = 0; i < result.length; i++) {
                
                var time_of_some_user = result[i].get("Times");
                console.log(result[i].get("Day"));
                console.log(result[i].get("User").id);
                console.log(time_of_some_user);
                
               
                // Iterate through user timeslots of a day
                for (var time_s = 0; time_s < time_of_some_user.length; time_s++) {
                    var t = time_of_some_user[time_s];
                    console.log(t);
          
                    // Pin the last hour
                    var last_hour = t[2];
                    var end_int = time_to_int(t[2],t[3]);
          
                    // Create temps of partial time, like [9,15,9,30] is one item
                    var hour = 9;
                    var temp_b = t.slice(0);
                    var temp_e = t.slice(0);
                    temp_e[2] = temp_e[0];
                    temp_e[3] = temp_e[1];
                     if (t[3] == 45) {
                        //temp_e[3] = 0;
                        //temp_e[2]++;
                    }
                    else {
                        temp_e[3] += 15;
                    } 
                    console.log("laatste uur: " + last_hour);
                    console.log(temp_b);
                    console.log(temp_e);
                 
                    // For every quarter in a day, set True if a user is free that time interval
                    while(hour <= 24) {
                        for (var minute = 0; minute < 59; minute += 15) { 
                            // User has the time interval in his AvailItem list
                            if (time_to_int(temp_b[0],temp_b[1])==time_to_int(hour,minute) && time_to_int(temp_b[0],temp_b[1]) < end_int) {  

                                // Necessary for neat time conversion
                                if (minute == 45) {
                                    temp_e[2]++;
                                    temp_e[3] = 0;
                                }
                                
                                // Set the time slot as true and log it
                                shared_time_personal[i][time_to_int(temp_b[0],temp_b[1])][4] = true;
                                console.log("Has " + temp_b[0] + temp_b[1] + " till " + temp_e[2] + temp_e[3]);
                                      
                                // Update temp items for next iteration
                                temp_b[0] = temp_e[2];
                                temp_b[1] = temp_e[3];
                                temp_e[3] += 15;                                                                                                   
                            }
                        }
                        hour++;
                    }
                }
            }
            
            console.log("Number of filled in times: " + result.length);
            // All users and slots are checked, derive the overlap
            for (var g = 0; g < 60; g++) {
                var all_persons = true;
                for (var user = 0; user < result.length; user++) {
                    
                    // If only one user doesn't have the time, it won't pass to shared_time
                    if (!shared_time_personal[user][g][4]) {
                        all_persons = false;
                    }
                }
                // All users have the slot, so put it eventually in an SharedTime Parse object
                if (all_persons)
                    shared_time[g][4] = true; 
            }
            
            var outputlist = [];
            for (var g = 0; g < 60; g++)
                if (shared_time[g][4]) {
                    console.log("GEMEENSCHAPPELIJK " + shared_time[g]);
                    outputlist.push(shared_time[g].slice(0,4));
                }
             
            // Get the corresponding SharedTime item, once created by the Initiator
            query3 = new Parse.Query("SharedTimes");
            query3.get(request.object.get("SharedTime").id, {
                success: function(sharedtime) {
                  console.log("succes" + sharedtime.length);
                  // Save the list of timeslot items
                  sharedtime.set("Times",outputlist);
                  sharedtime.save();
             
                },
                error: function(error) {
                  console.error("Got an error " + error.code + " : " + error.message);
                }
            });       
            
            // Double check: log the number of quarters a user is available
            for (var user = 0; user < result.length; user++) {
                var le = 0;
                for (var i = 0; i < shared_time_personal[user].length; i++)
                    if (shared_time_personal[user][i][4])
                        le++;
                console.log(le);
            }
        },
        error: function(e) {
            console.log("error");
            console.log(e);
        }
    }); 
 });
