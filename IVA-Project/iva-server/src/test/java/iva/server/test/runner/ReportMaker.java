package iva.server.test.runner;

import java.io.Closeable;

import iva.server.core.model.Question;
import iva.server.core.model.User;

public interface ReportMaker extends Closeable {

	void init(User testUser);

	void append(int index, Question query);

}