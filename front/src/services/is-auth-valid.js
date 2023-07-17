import APIRequest from "./fetch-service";
import Cookies from "js-cookie";

export async function isJwtValid() {
  const storedJwt = Cookies.get("jwt");
  var isJwtValid = false;
  const reqBody = {
    token: storedJwt,
  };

  // appel à l'api pour tester si les credentials sont toujours valides
  await APIRequest(
    `${process.env.PUBLIC_URL}/validate`,
    "POST",
    storedJwt,
    reqBody
  ).then(async (response) => {
    if (response.status === 200) {
      await response.json().then((data) => {
        isJwtValid = data.result;
      });
    }
  });
  return isJwtValid;
}
