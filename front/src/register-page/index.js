import React from "react";
import { useState } from "react";
import "../register-components/register.css";
import APIRequest from "../services/fetch-service";

function Register() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  async function handleSubmit(event) {
    event.preventDefault();
    try {
      await APIRequest("register", "POST", null, {
        username: username,
        password: password,
      }).then(
        async () => {
          alert("User Registration Successfully");
          setUsername("");
          setPassword("");
        }
      );
    } catch (err) {
      alert("User Registration Failed");
    }
    return (
      <div className="register-container">
        <form className="register-form" onSubmit={handleSubmit}>
          <br></br>
          <h1>Register</h1>
          <p>Fill in the information below</p>

          <input
            type="text"
            name="username"
            placeholder="username"
            onChange={(event) => {
              setUsername(event.target.value);
            }}
          />

          <input
            type="text"
            name="password"
            placeholder="password"
            onChange={(event) => {
              setPassword(event.target.value);
            }}
          />

          <button type="submit">Register</button>
        </form>
      </div>
    );
  }
}
export default Register;
