package app.itaycsguy.musiciansaidb

class User(details : HashMap<String,String>) {
    private val _details : HashMap<*, *> = details.clone() as HashMap<*, *>

    fun getUserName() : String {
        return this._details["user_name"].toString()
    }

    fun getEmail() : String {
        return this._details["email"].toString()
    }

    fun getPhoto() : String {
        return this._details["photo"].toString()
    }

    fun getGivenName() : String {
        return this._details["given_name"].toString()
    }

    fun getFamilyName() : String {
        return this._details["family_name"].toString()
    }
}