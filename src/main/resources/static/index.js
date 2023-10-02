function fetchTableData() {
    apiUrl = "/api/certificates/all"

    const response = fetch(apiUrl)
     .then(response => response.json())
     .then(data => {
     console.log(data);
       populateTable(data);
     })
     .catch(error => {
       console.error('Error fetching JSON data:', error);
     });
 }

 // Function to dynamically populate the table body with json data
 function populateTable(data) {
   const tableBody = document.querySelector('#certTable tbody');

   data.forEach(certificate => {
     const addRow = tableBody.insertRow();
     const url = addRow.insertCell(0);
     const expiryDate = addRow.insertCell(1);
     //const status = addRow.insertCell(2);
     const deleteRow = addRow.insertCell(2);

     const today = new Date();
     const expiryDateData = new Date(certificate.validTo);
     // dateString = expiryDateData.getFullYear() + " - " + expiryDateData.getMonth() + " - " + expiryDateData.getDate();

     // add cell content with json data
     url.textContent = certificate.url;
     expiryDate.textContent = certificate.validTo.substring(0,10);
    //  status.textContent = certificate.status;
     addRow.setAttribute('certificateId', certificate.id);

     // Expiration date calculation for visual notification
     const dateCalculate = Math.floor((expiryDateData - today) / (1000 * 60 * 60 * 24));

     // Visual notification based on the expiry date
     if (expiryDateData < today) {
       // Certificate has expired
       addRow.setAttribute('id', 'expired');
     } else if (dateCalculate < 14) {
       // Expiring within 2 weeks (less than 14 days)
       addRow.setAttribute('id', 'expiringInTwoWeeks');
     } else if (dateCalculate < 42) {
       // Expiring within 6 weeks (less than 42 days)
       addRow.setAttribute('id', 'expiringInSixWeeks');
     } else {
       // else (more than 6 weeks remaining)
       addRow.setAttribute('id', 'expiringGood');
     }

     // Deletion handling

     // Create button element for deleting the certificate
     const deleteButton = document.createElement("span");
     // deleteButton.textContent = 'DELETE';

     // Adding class to style the delete button
     deleteButton.classList.add('glyphicon');
     deleteButton.classList.add('glyphicon-trash');
     deleteButton.setAttribute('aria-hidden', 'True');

     // Append the element into the appropriate cell in the table
     deleteRow.appendChild(deleteButton);

     deleteButton.addEventListener('click', () => {
       const certificateId = addRow.getAttribute('certificateId');
       deleteFetch(certificateId);
     });
   });
 }

 // Function call to fetch all the list of certificates in the database
 fetchTableData();

// handling form submission (fetching POST request)
let submissionInProgress = false;
const form = document.querySelector('#submitUrl');
form.addEventListener('click', async function (e) {
  e.preventDefault();

//prevent multiple form submission while it's loading the data
  if (submissionInProgress) {
      return;
  }

  const userInput = document.querySelector("#userInputUrl").value;

  try {
    submissionInProgress = true;
    await addFetch(userInput);
    location.reload(); // Reload the page after the POST request
  } catch (error) {
    console.log(error);
  }
});

// POST request to API for sending URL to the backend
async function addFetch(userInputUrl) {
  let apiUrl = "/api/certificates/add";

  const response = await fetch(apiUrl, {
    method: 'POST',
    headers: {
      'Content-Type': "application/json"
    },
    body: JSON.stringify({ url: userInputUrl })
  });

  const data = await response.json();

  if (!response.ok) {
      throw new Error(data.message);
    }
  console.log(data);
}

 // Delete request to API, with ID# of the certificate to be deleted
 async function deleteFetch(certificateId){

   let apiUrl = `/api/certificates/delete/${certificateId}`;

   const response = await fetch(apiUrl, {
     method: 'DELETE',
     headers: {
       'Content-Type': 'application/json',
     }
   })
     .then((response) => {
       if (response.ok) {
         // Just refreshing the page will delete the row from the page, if it's deleted from the database.
         console.log("row deleted")
       } else {
         console.error('Failed to delete the certificate.');
       }
     })
     .catch((error) => {
       console.error('Error:', error);
     });
     location.reload();
 }