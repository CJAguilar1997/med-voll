alter TABLE medicos add activo tinyint;
update medicos set activo = 1;