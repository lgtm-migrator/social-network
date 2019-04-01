import React, {Component} from 'react';
import PropTypes from "prop-types";

/**
 * @class
 * User component
 */
class User extends Component {

    static propTypes = {
        /**
         * React router param
         */
        match: PropTypes.shape(
            /**
             * user id.
             */
            {id: PropTypes.number}
        ).isRequired
    };

    render() {
        return (
            <div>
                <h1>User page</h1>
                {this.props.match.params.id}
            </div>
        );
    }
}

export default User;