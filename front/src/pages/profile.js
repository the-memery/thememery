import React, { useContext, useState } from "react";
import Navbar from "../components/navbar";
import APIRequest from "../services/fetch-service";
import { useNavigate } from "react-router-dom";
import { Snackbar, Alert } from "@mui/material";
import { UserAuthContext } from "../App";
import Cookies from "js-cookie";

const Profile = () => {
  const { currentUser, logInHandler, logOutHandler } =
    useContext(UserAuthContext);
  const jwt = Cookies.get("jwt");
  const navigateTo = useNavigate();
  const [updatedUsername, setUpdatedUsername] = useState("");
  const [isSnackbarVisible, setIsSnackbarVisible] = useState(false);

  const handleCloseSnackbar = () => {
    setIsSnackbarVisible(false);
  };

  const toAdminPanel = () => {
    navigateTo(`/profile/admin`, {
      state: {
        currentUser: currentUser,
        role: currentUser.role,
      },
    });
  };

  const toMyArtworks = () => {
    navigateTo(`/profile/my-artworks`, {
      state: { currentUser: currentUser, role: currentUser.role },
    });
  };

  const toMyAddresses = () => {
    navigateTo(`/profile/address`, {
      state: { currentUser: currentUser, role: currentUser.role },
    });
  };

  return (
    <>
      <Navbar currentUser={currentUser} role={currentUser.role} />
      {/* <Link
        to="/address"
        state={{ currentUser: currentUser, role: currentRole }}
      >
        {" "}
        Address
      </Link> */}
      <div className="container">
        <div>
          <h4>
            <label htmlFor="text-form">username :</label>
          </h4>
          <input
            id="text-form"
            type="text"
            defaultValue={currentUser.username}
            onChange={(event) => {
              setUpdatedUsername(event.target.value);
            }}
          />
        </div>
        <div>
          <button
            onClick={async () => {
              // TODO ajouter une modal de react-bootstrap pour confirmer
              await UpdateCurrentUser();
            }}
          >
            rename
          </button>
          <Snackbar
            open={isSnackbarVisible}
            autoHideDuration={6000}
            onClose={handleCloseSnackbar}
          // action={action}
          >
            <Alert severity="error" sx={{ width: "100%" }}>
              Username is empty
            </Alert>
          </Snackbar>
        </div>
        {currentUser.role === 2 ? (
          <>
            <div>
              <button
                onClick={() => {
                  toAdminPanel();
                }}
              >
                manage website
              </button>
            </div>
          </>
        ) : (
          <></>
        )}
        {currentUser.role === 1 ? (
          <>
            <div>
              <button
                onClick={() => {
                  toMyArtworks();
                }}
              >
                my artworks
              </button>
            </div>
          </>
        ) : (
          <></>
        )}
        <div>
          <button
            onClick={() => {
              toMyAddresses();
            }}
          >
            my addresses
          </button>
        </div>
        <div>
          <button
            onClick={async () => {
              // TODO ajouter une modal de react-bootstrap pour confirmer
              await DeleteCurrentUser();
            }}
          >
            delete my account
          </button>
        </div>
      </div>
    </>
  );

  async function UpdateCurrentUser() {
    await APIRequest(`/user/${currentUser.id}`, "PUT", jwt, {
      username: updatedUsername,
    }).then((response) => {
      if (response.status === 200) {
        navigateTo("/log-in");
      } else {
        setIsSnackbarVisible(true);
      }
    });
  }

  async function DeleteCurrentUser() {
    await APIRequest(`/user/${currentUser.id}`, "DELETE", jwt).then(
      (response) => {
        if (response.status === 200) {
          navigateTo("/log-in");
        }
      }
    );
  }
};

export default Profile;
