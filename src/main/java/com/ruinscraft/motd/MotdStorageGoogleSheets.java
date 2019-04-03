package com.ruinscraft.motd;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;

public class MotdStorageGoogleSheets implements MotdStorage {

	private static final int A1_INDEX_FIRSTLINE 				= 0;
	private static final int A1_INDEX_SECONDLINE_ANNOUNCEMENTS 	= 1;
	private static final int A1_INDEX_SECONDLINE_TAGLINES 		= 2;
	private static final int A1_INDEX_SECONDLINE_TIPS 			= 3;

	private final GoogleCredential gCred;
	private final Sheets sheetsService;
	private final String sheetId;
	private final String A1_query;

	/* Cache */
	private List<String> firstLineCache = new ArrayList<>();
	private List<String> secondLineAnnouncementsCache = new ArrayList<>();
	private List<String> secondLineTagLinesCache = new ArrayList<>();
	private List<String> secondLineTipsCache = new ArrayList<>();

	public MotdStorageGoogleSheets(File credentialFile, String sheetId, String A1_query) throws IOException, GeneralSecurityException {
		gCred = GoogleCredential
				.fromStream(new FileInputStream(credentialFile))
				.createScoped(Collections.singletonList(SheetsScopes.SPREADSHEETS_READONLY));
		sheetsService = new Sheets(GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(), gCred);

		this.sheetId = sheetId;
		this.A1_query = A1_query;

		/* Cache update task */
		MotdPlugin.get().getProxy().getScheduler().schedule(MotdPlugin.get(), new CacheUpdate(), 0L, 1L, TimeUnit.HOURS);
	}

	private final class CacheUpdate implements Runnable {
		@Override
		public void run() {
			System.out.println("Updating MOTD cache...");

			firstLineCache.clear();
			secondLineAnnouncementsCache.clear();
			secondLineTagLinesCache.clear();
			secondLineTipsCache.clear();

			try {
				/* First line */
				ValueRange valueRange = sheetsService.spreadsheets().values().get(sheetId, A1_query).execute();
				List<List<Object>> values = valueRange.getValues();

				for (List<Object> row : values) {
					switch (row.size()) {
					case 4:
						if (row.get(A1_INDEX_SECONDLINE_TIPS) != null && !row.get(A1_INDEX_SECONDLINE_TIPS).toString().isEmpty()) {
							secondLineTipsCache.add(row.get(A1_INDEX_SECONDLINE_TIPS).toString());
						}
					case 3:
						if (row.get(A1_INDEX_SECONDLINE_TAGLINES) != null && !row.get(A1_INDEX_SECONDLINE_TAGLINES).toString().isEmpty()) {
							secondLineTagLinesCache.add(row.get(A1_INDEX_SECONDLINE_TAGLINES).toString());
						}
					case 2:
						if (row.get(A1_INDEX_SECONDLINE_ANNOUNCEMENTS) != null && !row.get(A1_INDEX_SECONDLINE_ANNOUNCEMENTS).toString().isEmpty()) {
							secondLineAnnouncementsCache.add(row.get(A1_INDEX_SECONDLINE_ANNOUNCEMENTS).toString());
						}
					case 1:
						if (row.get(A1_INDEX_FIRSTLINE) != null && !row.get(A1_INDEX_FIRSTLINE).toString().isEmpty()) {
							firstLineCache.add(row.get(A1_INDEX_FIRSTLINE).toString());
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void updateCache() {
		MotdPlugin.get().getProxy().getScheduler().runAsync(MotdPlugin.get(), new CacheUpdate());
	}

	@Override
	public Callable<List<String>> getFirstLines() {
		return () -> {
			return firstLineCache;
		};
	}

	@Override
	public Callable<List<String>> getSecondLineAnnouncements() {
		return () -> {
			return secondLineAnnouncementsCache;
		};
	}

	@Override
	public Callable<List<String>> getSecondLineTagLines() {
		return () -> {
			return secondLineTagLinesCache;
		};
	}

	@Override
	public Callable<List<String>> getSecondLineTips() {
		return () -> {
			return secondLineTipsCache;
		};
	}

}
