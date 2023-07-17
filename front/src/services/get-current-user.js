import APIRequest from "./fetch-service";

async function GetCurrentUser() {
  var currentUser = null;
  var role = 3;
  const jwt = localStorage.getItem("jwt");
  var tempUsers = [];

  await APIRequest("/user", "GET").then(async (response) => {
    await response.json().then((allUsers) => {
      tempUsers = allUsers;
    });
  });
  await APIRequest("/me", "GET", jwt).then(async (response) => {
    if (response.status === 401) {
      return { currentUser, role };
    }
    await response.json().then((userDetails) => {
      tempUsers.forEach((user) => {
        if (user.username === userDetails.username) {
          currentUser = user;
          switch (user.role) {
            case "ROLE_USER":
              role = 0;
              break;
            case "ROLE_ARTIST":
              role = 1;
              break;
            case "ROLE_ADMIN":
              role = 2;
              break;
            default:
              break;
          }
        }
      });
    });
  });

  return { currentUser, role };
}

export default GetCurrentUser;
