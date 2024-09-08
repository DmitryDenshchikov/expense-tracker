function submitExpense(event) {
  const data = new FormData(event.target);

  const formJSON = Object.fromEntries(data.entries());

  const expenseJson = JSON.stringify(formJSON);

  var xhr = new XMLHttpRequest();
  var url = "http://localhost:8080/expenses";
  xhr.open("POST", url, true);
  xhr.setRequestHeader("Content-Type", "application/json");
  xhr.onreadystatechange = function () {
      if (xhr.readyState === 4 && xhr.status === 200) {
          var json = JSON.parse(xhr.responseText);
          console.log(json.email + ", " + json.password);
      }
  };
  xhr.send(expenseJson);
}

const form = document.getElementById("expenseSubmissionForm");
form.addEventListener("submit", submitExpense);
