jQuery.sap.declare({
	modName : "component.home",
	type : "controller"
});
homeController = function() {

};
homeController.prototype.onInit = function() {
	var that = this;
	this._oViewModel = new sap.ui.model.json.JSONModel({});
	this.getView().setModel(this._oViewModel);
	var that = this;
	this.getView().byId("table").bindItems({
		path : "/",
		factory : function(sId, oContext) {
			return new sap.m.ColumnListItem({
				cells : [ new sap.m.Text({
					text : "{carId}"
				}), new sap.m.Text({
					text : "{location}"
				}), new sap.m.Text({
					text : "{color}"
				}), new sap.m.Text({
					text : "{isSpecial}"
				}) ]
			});
		}
	});

	$.ajax({
		url : "Service/findAllCar",
		success : function(aData) {

			that.getView().getModel().setData(aData);
		}

	});
};

sap.ui.controller("component.home", new homeController());
