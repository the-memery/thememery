import React from "react";
import { Navigate } from "react-router-dom";
import Request from "../services/fetchService"
// import { useLocalState } from "../util/useLocalStorage";


const PrivateRoute = ({ children }) => {
  const jwt = localStorage.getItem("jwt");
  if (jwt == null) {
    return <Navigate to="/" />;
  }
  const reqBody = {
    jwt: jwt,
  };
  return Request("validate", "POST", null, reqBody) ? children : <Navigate to="/" />;
};

export default PrivateRoute;