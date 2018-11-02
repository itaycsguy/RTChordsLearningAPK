package app.itaycsguy.musiciansaidb

class User(details : HashMap<String,String>) {
    private val _details : HashMap<*, *> = details.clone() as HashMap<*, *>

    fun getHashDetails() : HashMap<*, *> {
        return _details
    }

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

    fun getPermission() : String {
        return this._details["permission"].toString()
    }
}