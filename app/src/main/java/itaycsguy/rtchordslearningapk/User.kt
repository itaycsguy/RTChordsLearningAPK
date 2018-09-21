package itaycsguy.rtchordslearningapk

class User(details : HashMap<String,String>) {
    private val _details : HashMap<String,String> = details.clone() as HashMap<String,String>

    fun getUserName() : String {
        return this._details.get("user_name").toString()
    }

    fun getEmail() : String {
        return this._details.get("email").toString()
    }

    fun getPhoto() : String {
        return this._details.get("photo").toString()
    }

    fun getGivenName() : String {
        return this._details.get("given_name").toString()
    }

    fun getFamilyName() : String {
        return this._details.get("family_name").toString()
    }
}