
ObjectManager = new BaseObject();


// the following functions are also the ones a object handler should provide
function edit(id) {
	//starts editing a object
	ObjectManager.getObjectForId(id).edit();
}

function submit(id) {
	// submits the edited object as new. Reverts the object back to view state.
	// May create new object based on response.
	ObjectManager.getObjectForId(id).submit();
}

function unedit(id) {
	// reverts back the state of the object to view, canceling any unsubmitted edits
	ObjectManager.getObjectForId(id).unedit();
}

function search(request) {
	// searches for all objects where the attribute denoted by key ("type", "source", "dest") is id
	ObjectManager.search(request);
}
