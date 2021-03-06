import { HttpClient, HttpClientModule, HttpHeaders } 
     from '@angular/common/http';

import { Injectable } 
    from '@angular/core';

import { Observable, Subject } 
    from 'rxjs';


import { catchError, map, tap } 
    from 'rxjs/operators';

@Injectable()
export class ClientService {
    private headers: HttpHeaders;

    constructor( private http: HttpClient ){
        this.headers = new HttpHeaders(
            {
                'Access-Control-Allow-Origin':'*',
                'Content-Type': 'application/json',
                'X-CSRF-TOKEN': document.querySelector("meta[name='_csrf']").getAttribute("content")
            }
        );
    }

    addClient( obj ): Observable<any> {
        let encoded_data = JSON.stringify(obj);
        
        return this.http.post( 
            getBaseUri() + '/group/developer-tools/add-client.json', 
            encoded_data, 
            { headers: this.headers }
        )
        
    }

    editClient( obj ): Observable<any> {
        let encoded_data = JSON.stringify(obj);
        
        return this.http.post( 
            getBaseUri() + '/group/developer-tools/edit-client.json', 
            encoded_data, 
            { headers: this.headers }
        )
        
    }

    getClients(): Observable<any> {
        return this.http.get(
            getBaseUri() + '/group/developer-tools/get-clients.json'
        )
        
    }

    loadAvailableScopes(): Observable<any> {
        return this.http.get(
            getBaseUri() + '/group/developer-tools/get-available-scopes.json'
        )
        
    }

    resetClientSecret( obj ): Observable<any> {
        let encoded_data = JSON.stringify(obj);
        
        return this.http.post( 
            getBaseUri() + '/group/developer-tools/reset-client-secret.json', 
            encoded_data, 
            { headers: this.headers }
        )
        
    }

    showAddClient(): Observable<any> {
        return this.http.get(
            getBaseUri() + '/group/developer-tools/client.json'
        )
        
    }

    submitEditClient( obj ): Observable<any> {
        let encoded_data = JSON.stringify(obj);
        
        return this.http.post( 
            getBaseUri() + '/group/developer-tools/edit-client.json', 
            encoded_data, 
            { headers: this.headers }
        )
        
    }    
}
