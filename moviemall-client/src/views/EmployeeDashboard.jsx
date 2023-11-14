import {useEffect, useState} from "react";
import {fetchData} from "../utils/apiCaller";
import {SERVLET_ROUTE} from "../config/servletRoutes";
import {useAuth} from "../hooks/useAuth";

function EmployeeDashboard() {
    const [dbMetadata, setDbMetadata] = useState({});
    const { isLoggedIn, showLoginModal, setShowLoginModal, isChecking } = useAuth();

    useEffect(() => {
        fetchData(SERVLET_ROUTE.EMPLOYEE, null, false, "Error fetching database col metadata.")
            .then(response => {
                if (response.status === 200) {
                    setDbMetadata(response.data);
                }
            })
            .catch(error => console.error('Error:', error));
    }, [isLoggedIn]);

    if (!isLoggedIn) {
        return null;
    }

    return (
        <div className="employee-dashboard-container">
            <h1>moviedb Metadata</h1>
            {dbMetadata && Object.keys(dbMetadata).length > 0 && (
                Object.keys(dbMetadata).map(tableName => (
                    <div key={tableName}>
                        <h3 className="metadata-table-name">Table Name: {tableName}</h3>
                        <table>
                            <thead>
                            <tr>
                                <th>Column Name</th>
                                <th>Data Type</th>
                            </tr>
                            </thead>
                            <tbody>
                            {dbMetadata[tableName] && Array.isArray(dbMetadata[tableName]) && dbMetadata[tableName].map((column, index) => (
                                <tr key={index}>
                                    <td>{column.columnName}</td>
                                    <td>{column.dataType}</td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                    </div>
                ))
            )}
        </div>
    );
}



export default EmployeeDashboard;