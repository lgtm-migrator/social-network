import React, {Component} from 'react';
import {Col, Container, Jumbotron, Row} from "react-bootstrap";
import {ErrorMessage, Field, Form, Formik} from 'formik';
import axios from "axios";

class SignUp extends Component {
    render() {
        return (
            <Container>
                <Jumbotron>
                    <h1>Sign up page</h1>
                </Jumbotron>
                <Row>
                    <Col sm={12}>
                        <Formik
                            initialValues={{username: '', password: ''}}
                            validate={values => {
                                let errors = {};
                                //region validate username
                                if (!values.username) {
                                    errors.username = 'Required';
                                } else if (
                                    !/^[a-zA-Z0-9]{5,10}$/i.test(values.username)
                                ) {
                                    errors.username = 'Invalid username';
                                }
                                //endregion
                                //region Validate password
                                if (!values.password) {
                                    errors.password = 'Required';
                                } else if (
                                    !/^[a-zA-Z0-9]{8,12}$/i.test(values.password)
                                ) {
                                    errors.password = 'Invalid password';
                                }
                                //endregion
                                return errors;
                            }}
                            onSubmit={(values, {setSubmitting}) => {//?username=johndoe
                                axios.post(`http://localhost:8080/signup?username=${values.username}`, null, {
                                    headers: {
                                        "X-Password": values.password
                                    }
                                })
                                    .then(response => console.log("Server response", response))
                                    .catch(error => console.error("Server Error", error))
                                    .finally(() => setSubmitting(false));
                            }}
                        >
                            {({isSubmitting}) => (
                                <Form>
                                    <span>Username:</span>
                                    <label for="username">Username:</label>
                                    <Field type="username" name="username" autoComplete="username"/>
                                    <ErrorMessage name="username" component="div"/>
                                    <span>Password:</span>
                                    <label htmlFor="username">Username:</label>
                                    <Field type="password" name="password" autoComplete="current-password"/>
                                    <ErrorMessage name="password" component="div"/>
                                    <button type="submit" disabled={isSubmitting}>
                                        Submit
                                    </button>
                                </Form>
                            )}
                        </Formik>
                    </Col>
                </Row>

            </Container>
        );
    }
}

/*
* <Form.Group controlId="formBasicEmail">
    <Form.Label>Email address</Form.Label>
    <Form.Control type="email" placeholder="Enter email" />
    <Form.Text className="text-muted">
      We'll never share your email with anyone else.
    </Form.Text>
  </Form.Group>
* */
export default SignUp;