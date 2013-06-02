


// the following functions are also the ones a object handler should provide
function edit(id) {
	//starts editing a object
	ObjectManager.query({ id: id}).edit();
}

function submit(id) {
	// submits the edited object as new. Reverts the object back to view state.
	// May create new object based on response.
	ObjectManager.query({ id: id}).submit();
}

function unedit(id) {
	// reverts back the state of the object to view, canceling any unsubmitted edits
	ObjectManager.query({ id: id}).unedit();
}

function search(request) {
	// searches for all objects where the attribute denoted by key ("type", "source", "dest") is id
	ObjectManager.query(request);
}
