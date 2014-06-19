<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width">
  <link rel="stylesheet" href="http://code.jquery.com/mobile/1.4.2/jquery.mobile-1.4.2.min.css">
  <script src="http://code.jquery.com/jquery-1.10.2.min.js"></script>
  <script src="http://code.jquery.com/mobile/1.4.2/jquery.mobile-1.4.2.min.js"></script>
  <script type="text/javascript">

      function postCustomQuestionResult(){
        var username = document.getElementById('userInput_username').value;
        var password = document.getElementById('userInput_password').value;
        var eventId = $("#userSelect_eventId option:selected").val();
        var eventIdText = $("#userSelect_eventId option:selected").text();
        var questionId = $("#userSelect_questionId option:selected").val();
        var questionIdText = $("#userSelect_questionId option:selected").text();
        var winnerPick = $("#userSelect_winnerPick option:selected").val();
        var winnerPickText = $("#userSelect_winnerPick option:selected").text();
        
        var payload =  '{"username": ' + username + ', "password": ' + password + ', "eventId":' + eventId + ', "questionId":' + questionId + ', "winnerPick":' + winnerPick + '}';
        var confirmBoxText = "\n username: " + username + "\n password: (hidden)" + "\n event: " + eventIdText + "\n question: " + questionIdText + "\n winnerPick: " + winnerPickText;
        
        var customQuestionResultUrl = "";
        if ($(location).attr('hostname') == 'localhost'){
          customQuestionResultUrl='http://'+ $(location).attr('host')+'/scorena/v1/admin/game/customquestionresult';
        }else{
          customQuestionResultUrl='http://'+ $(location).attr('host')+'/v1/admin/game/customquestionresult';
        }
        if (confirm("Are you sure you want to submit ths following result? " + confirmBoxText) == true){
          $.ajax({
              type: "POST",
              async: false,
              url: customQuestionResultUrl,
              data: payload,
              dataType: "json",
              contentType: "application/json; charset=utf-8",
              success: function (suc) {
                alert('Custom question result posted successfully!') 
              },
              error: function (err) { 
                alert("ERROR! Error response text: " + err.responseText)
              }
          });
        } else {
            alert("Cancelled: the result was NOT posted.");
        }
      };

      function getPastGames() {
        var eventIdSelectionMenu = document.getElementById('userSelect_eventId');
        var placeHolderOption = document.createElement('option');
        placeHolderOption.text = "--Please select a game--";
        placeHolderOption.value = "";
        placeHolderOption.id = "new_PlaceHolderOption_getPastGames";
        placeHolderOption.selected = true;

        //removed old options
        $(eventIdSelectionMenu) 
          .find('option')
          .remove()
          .end()

        var getPastGamesUrl = "";

        //check for the right url to use
        if ($(location).attr('hostname') == 'localhost'){
          getPastGamesUrl='http://'+ $(location).attr('host')+'/scorena/v1/sports/soccer/leagues/all/games/past';
        }else{
          getPastGamesUrl='http://'+ $(location).attr('host')+'/v1/sports/soccer/leagues/all/games/past';
        }

        $.getJSON(getPastGamesUrl, function(pastGames) {
          eventIdSelectionMenu.add(placeHolderOption);
          $.each(pastGames, function (key, eachGame){
            var newOption = document.createElement('option');
            newOption.text = eachGame.leagueName + ": " + eachGame.home.teamname + " v.s " + eachGame.away.teamname + " @ " + eachGame.date;
            newOption.value = eachGame.gameId;
            eventIdSelectionMenu.add(newOption);
            });
          $("select").change();
        })
        .done(function (json) {
          alert("getPastGames: Success");
        })
        .fail(function (jqxhr, textStatus, error) {
          alert("ErrorStatus: " + textStatus + ". Error message: " + error);
        });

        //add a last updated message
        var updateText ="Past games last updated: " + Date();
        document.getElementById("update_header").innerHTML = updateText;
      };

      function getQuestions(){
        var eventIdSelected = $("#userSelect_eventId option:selected").val();
        var questionIdSelectionMenu = document.getElementById('userSelect_questionId');
        //var winnerPickSelectionMenu = document.getElementById('userSelect_winnerPick');
        var placeHolderOption = document.createElement('option');
        placeHolderOption.text = "--Please select a question--";
        placeHolderOption.value = "";
        placeHolderOption.id = "new_PlaceHolderOption_getQuestions";
        placeHolderOption.selected = true;

        //removed old options
        $(questionIdSelectionMenu) 
          .find('option')
          .remove()
          .end()

        //check if the eventId is invalid
        if (eventIdSelected == null || eventIdSelected.length == 0){
          questionIdSelectionMenu.add(placeHolderOption);
          return null;
        }

        var getQuestionsUrl = "";

        //check for the right url to use
        if ($(location).attr('hostname') == 'localhost'){
          getQuestionsUrl='http://'+ $(location).attr('host') +'/scorena/v1/sports/soccer/leagues/all/games/' + eventIdSelected+ '/qs';
        }else{
          getQuestionsUrl='http://'+ $(location).attr('host')+'/v1/sports/soccer/leagues/all/games/' + eventIdSelected+ '/qs';
        }

        $.getJSON(getQuestionsUrl, function(pastQuestions) {
          questionIdSelectionMenu.add(placeHolderOption);
          $.each(pastQuestions, function (key, eachQuestion){
            var newOption = document.createElement('option');
            newOption.text = eachQuestion.content;
            newOption.value = eachQuestion.questionId;
            questionIdSelectionMenu.add(newOption);
            });
          $("#userSelect_questionId").change();
        })
        .done(function (json) {
          alert("getQuestions: Success");
        })
        .fail(function (jqxhr, textStatus, error) {
          alert("ErrorStatus: " + textStatus + ". Error message: " + error);
        });
      };

      function getPicks(){
        var eventIdSelected = $("#userSelect_eventId option:selected").val();
        var questionIdSelected = $("#userSelect_questionId option:selected").val();
        var winnerPickSelectionMenu = document.getElementById('userSelect_winnerPick');

        var placeHolderOption = document.createElement('option');
        placeHolderOption.text = "--Please select the winnerPick--";
        placeHolderOption.value = "";
        placeHolderOption.id = "new_PlaceHolderOption_getPicks";
        placeHolderOption.selected = true;

        //removed old options
        $(winnerPickSelectionMenu) 
          .find('option')
          .remove()
          .end()

        //check if the eventId and/or questionId is invalid
        if (eventIdSelected == null || eventIdSelected.length == 0 || questionIdSelected == null || questionIdSelected.length == 0){
          winnerPickSelectionMenu.add(placeHolderOption);
          return null;
        }
        

        var getPicksUrl = "";

        //check for the right url to use
        if ($(location).attr('hostname') == 'localhost'){
          getPicksUrl='http://'+ $(location).attr('host') +'/scorena/v1/sports/soccer/leagues/all/games/' + eventIdSelected+ '/qs/' + questionIdSelected;
        }else{
          getPicksUrl='http://'+ $(location).attr('host')+'/v1/sports/soccer/leagues/all/games/' + eventIdSelected+ '/qs/' + questionIdSelected;
        }

        $.getJSON(getPicksUrl, function(selectedQuestion) {
          winnerPickSelectionMenu.add(placeHolderOption);

          var pick0Option = document.createElement('option');
          pick0Option.text = "Pick 0: result is a tie";
          pick0Option.value = "0";
          winnerPickSelectionMenu.add(pick0Option);

          var pick1Option = document.createElement('option');
          pick1Option.text = "Pick 1: " + selectedQuestion.pick1;
          pick1Option.value = "1";
          winnerPickSelectionMenu.add(pick1Option);

          var pick2Option = document.createElement('option');
          pick2Option.text = "Pick 2: " + selectedQuestion.pick2;
          pick2Option.value = "2";
          winnerPickSelectionMenu.add(pick2Option);

          $("#userSelect_winnerPick").change();
        })
        .done(function (json) {
          alert("getPicks: Success");
        })
        .fail(function (jqxhr, textStatus, error) {
          alert("ErrorStatus: " + textStatus + ". Error message: " + error);
        });
      };
      
   </script>
   <title> Scorena Custom Question Result Web Client </title>
</head>
<body>
<div data-role="page" id="main_Page">
  <div data-role="header">
    
    <p> Scorena Custom Question Result Web Client </p>
    <p id = "update_header"> Past games last updated: has not retrieved any games </p>
  </div> <!-- header -->
  <div data-role="main" class="ui-content">
      <form method="post" name="customQuestionForm">
          <div class="ui-field-contain">
            <label for="username">Username:</label>
            <input type="text" name="username" id="userInput_username" placeholder="Username">

            <label for="password">Password:</label>
            <input type="password" name="password" id="userInput_password" placeholder="Password">

            <label for="eventId">Event ID:</label>
            <select name="eventId" id="userSelect_eventId" onchange="getQuestions()">
              <option value = "" selected> --Please select a game-- </option>
            </select>

            <label for="questionId">QuestionId:</label>
            <select name="questionId" id="userSelect_questionId" onchange="getPicks()">
              <option value = "" selected> --Please select a question-- </option>
            </select>

            <label for="winnerPick">Winner pick:</label>
            <select name="winnerPick" id="userSelect_winnerPick">
              <option value = "" selected> --Please select the winnerPick-- </option>
            </select>
          </div>
          <button id="userClick_getPastGames" onclick="getPastGames (); return false;"> getPastGames or refresh</button>
          <button id="userInput_postButton" onclick="postCustomQuestionResult (); return false;"> Post</button>
        </form>
  </div> <!-- /content -->
</div>

</body>

</html>