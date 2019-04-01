import React, {Component} from 'react';
import {BrowserRouter as Router, Route} from "react-router-dom";
import {Button, Form, Nav, Navbar} from "react-bootstrap";
import './App.css';
import SignUp from "./SignUp";
import User from "./User";

class App extends Component {
    render() {
        return (
            <Router>
                <Navbar>
                    <Navbar.Brand href="#home">Social Network</Navbar.Brand>
                    <Nav className="mr-auto">
                        <Nav.Link href="#home">Home</Nav.Link>
                    </Nav>
                    <Form inline>
                        <Form.Control type="text" placeholder="username" className="mr-sm-2" size="sm"/>
                        <Form.Control type="password" placeholder="password" className="mr-sm-2" size="sm"/>
                        <Button variant="outline-info" size="sm">Sign in</Button>
                    </Form>
                </Navbar>
                <Route path="/signup" component={SignUp}/>
                <Route path="/user/:id" component={User}/>
            </Router>
        );
    }
}

export default App;
