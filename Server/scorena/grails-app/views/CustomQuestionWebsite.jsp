<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width">
  <link rel="stylesheet" href="http://code.jquery.com/mobile/1.4.2/jquery.mobile-1.4.2.min.css">
  <script src="http://code.jquery.com/jquery-1.10.2.min.js"></script>
  <script src="http://code.jquery.com/mobile/1.4.2/jquery.mobile-1.4.2.min.js"></script>
  <script type="text/javascript">
      var eventId = "";
      function customQuestion(){
        var username = document.getElementById('userInput_username').value;
        var password = document.getElementById('userInput_password').value;
        var qContent = document.getElementById('userInput_qContent').value;
        var pick1 = document.getElementById('userInput_pick1').value;
        var pick2 = document.getElementById('userInput_pick2').value;
        
        var payload =  '{"username": ' + username + ' , "qContent": ' + qContent + ' , "password": ' + password + ', "pick1":' + pick1 + ', "pick2":' + pick2 + ', "eventId":' + eventId +'}';
        var customQuestionUrl = "";
        if ($(location).attr('hostname') == 'localhost'){
          customQuestionUrl='http://'+ $(location).attr('host')+'/scorena/v1/admin/game/customquestion';
        }else{
          customQuestionUrl='http://'+ $(location).attr('host')+'/v1/admin/game/customquestion';
        }
            $.ajax({
              type: "POST",
              async: true,
              url: customQuestionUrl,
              data: payload,
              dataType: "json",
              contentType: "application/json; charset=utf-8",
              success: function (suc) {
                alert('Custom question submitted successfully!') 
              },
              error: function (err) { 
                alert("ERROR! Error response text: " + err.responseText)
              }
        	});
      };

      function getUpcomingGames() {
        
        var gameIdSelectionMenu = document.getElementById('userSelect_eventId');
        var placeHolderOption = document.createElement('option');
        placeHolderOption.text = "--Please select a game--";
        placeHolderOption.value = "";
        placeHolderOption.id = "new_PlaceHolderOption";
        placeHolderOption.selected = true;
        //removed old options
        $(gameIdSelectionMenu) 
          .find('option')
          .remove()
          .end()
        var getUpcomingGamesUrl = "";

        //check for the right url to use
        if ($(location).attr('hostname') == 'localhost'){
          getUpcomingGamesUrl='http://'+ $(location).attr('host')+'/scorena/v1/sports/soccer/leagues/all/games/upcoming';
        }else{
          getUpcomingGamesUrl='http://'+ $(location).attr('host')+'/v1/sports/soccer/leagues/all/games/upcoming';
        }

        $.getJSON(getUpcomingGamesUrl, function(upcomingGames) {
          gameIdSelectionMenu.add(placeHolderOption);
          $.each(upcomingGames, function (key, eachGame){
            var newOption = document.createElement('option');
            newOption.text = eachGame.leagueName + ": " + eachGame.home.teamname + " v.s " + eachGame.away.teamname + " @ " + eachGame.date;
            newOption.value = eachGame.gameId;
            gameIdSelectionMenu.add(newOption);
            });
          $("select").change();

        })
        .done(function (json) {
          alert ("getUpcomingGames Success");
        })
        .fail(function (jqxhr, textStatus, error) {
          alert ("ErrorStatus: " + textStatus + ". Error message: " + error);
        });

        //add a last updated message
        var updateText ="Upcoming games last updated: " + Date();
        document.getElementById("update_header").innerHTML = updateText;
      };

      //dynamicaly changed the value of the select/option id "userSelect_eventId" 
      function getSelectedGame(chosenGame) {
        eventId = chosenGame.value;
      };
      
   </script>
</head>
<body>
<div data-role="page" id="main_Page">
  <div data-role="header">
    <h1> Scorena Custom Game Web Client </h1>
    <p id = "update_header"> Upcoming games last updated: has not retrieved any games </p>
  </div> <!-- header -->
  <div data-role="main" class="ui-content">
      <form method="post" name="customQuestionForm">
          <div class="ui-field-contain">
            <label for="username">Username:</label>
            <input type="text" name="username" id="userInput_username" placeholder="Username">

            <label for="password">Password:</label>
            <input type="password" name="password" id="userInput_password" placeholder="Password">

            <label for="qContent">Question Content:</label>
            <input type="text" name="qContent" id="userInput_qContent" placeholder="Question Content">

            <label for="pick1">Pick 1:</label>
            <input type="text" name="pick1" id="userInput_pick1" placeholder="pick1">

            <label for="pick2">Pick 2:</label>
            <input type="text" name="pick2" id="userInput_pick2" placeholder="pick2">

            <label for="eventId">Event ID:</label>
            <select name="eventId" id="userSelect_eventId" placeholder="eventId" onchange="getSelectedGame(this)">
              <option selected>--Please select a game--</option>
            </select>
          </div>
          <button id="userClick_getUpcomingGames" onclick="getUpcomingGames (); return false;"> getUpcomingGames or refresh</button>
          <button id="userInput_sumbitButton" onclick="customQuestion (); return false;"> Submit</button>
        </form>
  </div> <!-- /content -->
</div>

</body>

</html>