import React, { useContext, useState } from "react";
import Info from "../components/info";
import APIRequest from "../services/fetch-service";
import { useNavigate } from "react-router-dom";
import { Snackbar, Alert } from "@mui/material";
import { UserAuthContext } from "../App";
import Cookies from "js-cookie";

const LogIn = () => {
  const navigateTo = useNavigate();
  const { currentUser, logInHandler, logOutHandler } =
    useContext(UserAuthContext);
  var isConnected = false; // si vrai on redirige vers la homepage
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [isSnackbarVisible, setIsSnackbarVisible] = useState(false);

  const handleCloseSnackbar = () => {
    setIsSnackbarVisible(false);
  };

  const toHomePage = () => {
    navigateTo("/");
  };

  async function TryLogIn() {
    var currentUser = null;
    const reqBody = {
      username: username,
      password: password,
    };

    await APIRequest("authenticate", "POST", null, reqBody).then(
      async (response) => {
        if (response.status === 200) {
          await response.json().then((data) => {
            Cookies.set("jwt", data.token, {
              expires: 1,
              secure: true,
            });
            isConnected = true;
          });
        }
      }
    );

    if (!isConnected) {
      return;
    }

    await APIRequest("me", "GET", Cookies.get("jwt")).then(async (response) => {
      if (response.status === 200) {
        await response.json().then((userDetails) => {
          currentUser = userDetails;
        });
      }
    });
    return currentUser;
  }

  const handleSubmit = async (e) => {
    e.preventDefault();
  };

  return (
    <div className="container-login">
      <Info />
      <label htmlFor="username-form"> username </label>
      <br />
      <form onSubmit={handleSubmit}>
        <input
          type="text"
          id="username-form"
          autoComplete="off"
          required={true}
          onChange={(event) => {
            setUsername(event.target.value);
          }}
        />
        <br />
        <label htmlFor="text-form"> password </label>
        <input
          type="password"
          id="text-form"
          autoComplete="off"
          required={true}
          onChange={(event) => {
            setPassword(event.target.value);
          }}
        />
        <br />
        <br />
        <a href="/register">new here ? sign up</a>
        <br />
        <br />
        <button
          className="button-login"
          onClick={async () => {
            logInHandler(await TryLogIn());
            if (isConnected) {
              toHomePage();
            } else {
              setIsSnackbarVisible(true);
            }
          }}
        >
          log in
        </button>
        {/* <Link to={"/"}>Log In</Link> */}
        {/* <LogInCustomLink to="/">Log In</LogInCustomLink> */}
      </form>
      <Snackbar
        open={isSnackbarVisible}
        autoHideDuration={6000}
        onClose={handleCloseSnackbar}
      >
        <Alert severity="error" sx={{ width: "100%" }}>
          Invalid Credentials
        </Alert>
      </Snackbar>
    </div>
  );
};

export default LogIn;
