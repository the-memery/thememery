import React, { useContext } from "react";
import { Link, useMatch, useResolvedPath } from "react-router-dom";
import { UserAuthContext } from "../App";
import "../styles/navbar.css";

const Navbar = () => {
  const { currentUser, logOutHandler } =
    useContext(UserAuthContext);

  return (
    <nav className="navbar">
      <div className="container-navbar">

        <label className="drop-down-menu">

          <div className="dd-button">👅</div>

          <input type="checkbox" class="dd-input" id="home-dd-input"></input>

          <ul className="dd-content">
            <li><CustomLink to="/artists">artists</CustomLink></li>
            <li><CustomLink to="/e-commerce">boutique</CustomLink></li>
            <li className="divider"></li>
            {currentUser.role === 3 ? (
              <li><CustomLink to="/log-in">log in</CustomLink></li>) : (
              <>
                <li><CustomLink to="/profile">profile</CustomLink></li>
                <li><CustomLink onClick={logOutHandler} to="/">log out</CustomLink></li>
              </>
            )}
          </ul>

        </label>

        <ul className="items-wrap">
          <li className="nav-item">
            <CustomLink to="/artists">artists</CustomLink>
          </li>
          <li className="nav-item">
            <CustomLink to="/e-commerce" props={{ currentUser: currentUser }}>
              boutique
            </CustomLink>
          </li>
        </ul>
        <div className="navbar-logo">
          <Link to="/">THE MEMERY</Link>
        </div>
        <ul className="items-wrap">
          <li className="nav-item">
            {currentUser.role !== 3 ? (
              <CustomLink to="/profile">
                profile
                {/* <MdPerson /> */}
              </CustomLink>
            ) : (
              <></>
            )}
          </li>
          <li className="nav-item">
            {currentUser.role !== 3 ? (
              <CustomLink onClick={logOutHandler} to="/">
                log out
              </CustomLink>
            ) : (
              <CustomLink to="/log-in">log in</CustomLink>
            )}
          </li>
        </ul>
      </div>
    </nav>
  );
};

//cette fonction permet d'encapsuler plusieurs choses dans une balise qui s'appelle CustomLink qui permet d'avoir un code plus propre
function CustomLink({ to, children, props, onClick }) {
  const resolvedPath = useResolvedPath(to);
  const isActive = useMatch({ path: resolvedPath.pathname, end: true });

  return (
    <div className={`nav-item ${isActive ? "active" : ""}`}>
      <Link to={to} state={{ ...props }} onClick={onClick} className="nav-link">
        {children}
      </Link>
    </div>
  );
}

export default Navbar;
