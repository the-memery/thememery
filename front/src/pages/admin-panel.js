import React, { useState, useEffect } from "react";
import { useLocation } from "react-router-dom";
import Navbar from "../components/navbar";
import {
  Grid,
  Box,
  InputLabel,
  MenuItem,
  FormControl,
  Select,
  Stack,
  Tab,
  Card,
  Divider,
  Snackbar,
  Alert,
} from "@mui/material";
import { TabContext, TabList, TabPanel } from "@mui/lab";
import APIRequest from "../services/fetch-service";
import { Button } from "react-bootstrap";
import "react-calendar/dist/Calendar.css";
import getAllUserRole from "../util/all-user-role";
import getAllEventType from "../util/all-event-type";
import SimpleDialog from "../components/create-event-dialog";
import ConstructDate from "../util/construct-date";

// TODO si on a le temps
// ne pas modifier utilisateur si on change le nom quelque part et on clique sur le bouton update d'un autre

const AdminPanel = () => {
  const jwt = localStorage.getItem("jwt");
  const location = useLocation();
  const [currentUser, setCurrentUser] = useState({});
  const [currentRole, setCurrentRole] = useState(3);
  const [users, setUsers] = useState([]);
  const [events, setEvents] = useState([]);
  const [tabValue, setTabValue] = useState("1");

  const [updatedUsername, setUpdatedUsername] = useState("");
  const [updatedRole, setUpdatedRole] = useState();
  const [selectedEvent, setSelectedEvent] = useState({});

  const [isDialogVisible, setIsDialogVisible] = useState(false);
  const [isFailureSnackbarVisible, setIsFailureSnackbarVisible] =
    useState(false);
  const [isSuccessSnackbarVisible, setIsSuccessSnackbarVisible] =
    useState(false);

  const handleCloseSnackbar = () => {
    setIsFailureSnackbarVisible(false);
  };

  const handleClickOpen = () => {
    setIsDialogVisible(true);
  };
  const handleClose = async () => {
    await getAllEvent();
    setIsDialogVisible(false);
  };

  const tabValueChange = (event, newValue) => {
    setTabValue(newValue);
  };

  async function getAllEvent() {
    // request GET all events
    await APIRequest("/event", "GET", jwt).then(async (response) => {
      await response.json().then((allEvents) => {
        setEvents([...allEvents]);
      });
    });
  }

  useEffect(() => {
    async function setup() {
      setCurrentUser(location.state.currentUser);
      setCurrentRole(location.state.role);

      // request GET all users
      await APIRequest("/user", "GET", jwt).then(async (response) => {
        await response.json().then((allUsers) => {
          setUsers([...allUsers]);
        });
      });
    }

    setup();
    getAllEvent();
  }, []);

  return (
    <>
      <Navbar currentUser={currentUser} role={currentRole} />

      <TabContext value={tabValue}>
        <TabList centered={true} onChange={tabValueChange}>
          <Tab label="users" value="1" />
          <Tab label="events" value="2" />
        </TabList>
        <TabPanel value="1">
          <Grid container spacing={2} direction={"column"}>
            {users.map((users, index) => {
              const { username, role } = users;
              return (
                <Grid item key={index}>
                  <Stack direction={"row"} spacing={2} minHeight={56}>
                    <input
                      type="text"
                      defaultValue={username}
                      onChange={(event) => {
                        console.log(event.target.value);
                        setUpdatedUsername(event.target.value);
                      }}
                    />
                    <Box sx={{ minWidth: 120, maxHeight: 10 }}>
                      <FormControl fullWidth>
                        <InputLabel id="demo-simple-select-label">
                          role
                        </InputLabel>
                        <Select
                          labelId="demo-simple-select-label"
                          id="demo-simple-select"
                          label="role"
                          defaultValue={getAllUserRole(role)}
                          // value={updatedRole}
                          onChange={(event) => {
                            setUpdatedRole(event.target.value);
                            setUpdatedUsername(username);
                          }}
                        >
                          {getAllUserRole("").map((userRole, index) => {
                            return (
                              <MenuItem key={index} value={index}>
                                {userRole}
                              </MenuItem>
                            );
                          })}
                        </Select>
                      </FormControl>
                    </Box>
                    <Button
                      onClick={async () => {
                        // TODO ajouter une modal de react-bootstrap pour confirmer
                        await UpdateUser(
                          users.id,
                          updatedUsername,
                          updatedRole
                        );
                      }}
                    >
                      update user
                    </Button>{" "}
                    <Button
                      onClick={async () => {
                        // TODO ajouter une modal de react-bootstrap pour confirmer
                        await DeleteUser(users.id);
                      }}
                    >
                      delete user
                    </Button>
                  </Stack>
                </Grid>
              );
            })}
          </Grid>
          <Snackbar
            open={isFailureSnackbarVisible}
            autoHideDuration={6000}
            onClose={handleCloseSnackbar}
          // action={action}
          >
            <Alert severity="error" sx={{ width: "100%" }}>
              Username is empty
            </Alert>
          </Snackbar>
          <Snackbar
            open={isSuccessSnackbarVisible}
            autoHideDuration={6000}
            onClose={handleCloseSnackbar}
          // action={action}
          >
            <Alert severity="success" sx={{ width: "100%" }}>
              User updated
            </Alert>
          </Snackbar>
        </TabPanel>

        <TabPanel value="2">
          <button
            onClick={() => {
              // setIsCreation(true);
              handleClickOpen();
            }}
          >
            new
          </button>
          <SimpleDialog
            // selectedValue={selectedValue}
            open={isDialogVisible}
            currentUser={currentUser}
            // isCreation={isCreation}
            setEvents={setEvents}
            event={selectedEvent}
            onClose={handleClose}
          />

          <Box m={3}>
            <Divider variant="middle"></Divider>
          </Box>
          <Grid container spacing={4}>
            {events.map((event, index) => {
              const { id, name, type, date, image, address } = event;
              // console.log(date);
              return (
                <Grid item key={index}>
                  <Card style={{ width: 330, height: 550 }}>
                    <Stack spacing={2} style={{ margin: 15 }}>
                      <Stack direction={"row"} justifyContent={"space-between"}>
                        <label style={{ margin: 15 }}>
                          <b>{name}</b>
                        </label>
                        <label style={{ margin: 15 }}>
                          {getAllEventType("")[getAllEventType(type)]}
                        </label>
                      </Stack>
                      <img
                        src={`data:image/png;base64, ${image}`}
                        width={300}
                      ></img>
                      <label>on : {ConstructDate(date).toDateString()}</label>
                      <label>
                        at : {address.street} {address.postalCode}
                      </label>
                      <label>
                        {address.city}, {address.country}
                      </label>
                      {/* <Button
                        style={{ alignSelf: "center" }}
                        onClick={async () => {
                          setSelectedEvent(event);
                          // setIsCreation(false);
                          handleClickOpen();
                        }}
                      >
                        update
                      </Button> */}
                      <Button
                        style={{ alignSelf: "center" }}
                        onClick={async () => {
                          DeleteEvent(id);
                        }}
                      >
                        delete
                      </Button>
                    </Stack>
                  </Card>
                </Grid>
              );
            })}
          </Grid>
        </TabPanel>
      </TabContext>

      {/* <Divider variant="middle" /> */}
    </>
  );

  async function UpdateUser(id, username, role) {
    var body = {};
    if (username !== "") {
      body.username = username;
    }
    if (role !== null) {
      body.role = role;
    }
    await APIRequest(`/user/${id}`, "PUT", jwt, {
      username: username,
      role: role,
    }).then((response) => {
      if (response.status === 200) {
        setIsSuccessSnackbarVisible(true);
      } else {
        setIsFailureSnackbarVisible(true);
      }
    });
  }

  async function DeleteUser(id) {
    await APIRequest(`/user/${id}`, "DELETE", jwt).then((response) => {
      if (response.status === 200) {
      }
    });
  }

  async function DeleteEvent(id) {
    await APIRequest(`/event/${id}`, "DELETE", jwt).then((response) => {
      if (response.status === 200) {
        console.log("event deleted");
        getAllEvent();
      }
    });
  }
};

export default AdminPanel;
